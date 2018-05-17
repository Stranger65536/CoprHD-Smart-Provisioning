/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.dao.mongo.VirtualPool;
import com.emc.coprhd.sp.dao.mongo.VirtualPool.VirtualPoolBuilder;
import com.emc.coprhd.sp.json.vnxszier.ApplicationWorkloads;
import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.emc.coprhd.sp.json.vnxszier.Pool;
import com.emc.coprhd.sp.json.vnxszier.PoolInfo;
import com.emc.coprhd.sp.json.vnxszier.SystemPreferences;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.repository.mongo.MongoDao;
import com.emc.coprhd.sp.service.sizer.VNXSizerClient;
import com.emc.coprhd.sp.service.srm.SRMClient;
import com.emc.coprhd.sp.service.vipr.ViPRClient;
import com.emc.coprhd.sp.supports.vnx.sizer.SystemFlareVersion;
import com.emc.coprhd.sp.supports.vnx.sizer.SystemType;
import com.emc.coprhd.sp.supports.vnx.sizer.TierRaidType;
import com.emc.coprhd.sp.transfer.client.request.ApplyWorkloadRequest;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.request.ProvisionLunRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo.StoragePoolPerformanceInfoBuilder;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;
import com.emc.coprhd.sp.transfer.vnx.sizer.request.VNXSizerRequest;
import com.emc.coprhd.sp.transfer.vnx.sizer.response.VNXSizerResponse;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.hazelcast.util.ExceptionUtil.sneakyThrow;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Service
@SuppressWarnings("OverlyCoupledClass")
public class ProcessingServiceImpl implements ProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingServiceImpl.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    private static final int CALCULATION_SUCCESS = 0;
    private static final Pattern SS = Pattern.compile("5500", Pattern.LITERAL);

    private final String nodeId;
    private final byte[] requestTemplate;
    private final ViPRClient viprClient;
    private final VNXSizerClient vnxSizerclient;
    private final SRMClient srmClient;
    private final MongoDao mongoDao;

    @Autowired
    public ProcessingServiceImpl(
            @Value("${com.emc.coprhd.sp.node-id}") final String nodeId,
            @Value("${com.emc.coprhd.sp.sizer.template}") final String vnxSizerTemplateFile,
            final ViPRClient viprClient,
            final VNXSizerClient vnxSizerclient,
            final SRMClient srmClient,
            final MongoDao mongoDao)
            throws IOException {
        this.nodeId = nodeId;
        this.viprClient = viprClient;
        this.vnxSizerclient = vnxSizerclient;
        this.srmClient = srmClient;
        this.mongoDao = mongoDao;
        final File requestTemplateFile = new File(checkNotNull(vnxSizerTemplateFile,
                "JSON template path must be specified!"));
        checkState(requestTemplateFile.exists(), "JSON template file must exist!");
        this.requestTemplate = Files.readAllBytes(Paths.get(requestTemplateFile.toURI()));
    }

    @Override
    public StoragePoolsInfo getStoragePoolsInfo() {
        final Collection<StoragePoolRestRep> storagePools = viprClient.getStoragePools();

        final List<StoragePoolPerformanceInfo> storagePoolsPerformanceInfo = new ArrayList<>(storagePools.size());
        final Map<String, StoragePoolRestRep> storagePoolsDetailedInfoMap = new HashMap<>(storagePools.size(), 1.0f);
        final Map<String, StorageSystemRestRep> storageSystemsMap = new HashMap<>(storagePools.size(), 1.0f);

        for (StoragePoolRestRep storagePool : storagePools) {
            final ViPRInfo viprInfo = getViPRInfo(storagePool);

            storagePoolsDetailedInfoMap.put(storagePool.getId().toString(), viprInfo.getStoragePoolDetailedInfo());
            storageSystemsMap.put(storagePool.getId().toString(), viprInfo.getStorageSystem());

            final SRMPoolInfo srmPoolInfo = getSRMPoolInfo(storagePool);

            storagePoolsPerformanceInfo.add(StoragePoolPerformanceInfoBuilder
                    .aStoragePoolPerformanceInfo()
                    .withId(viprInfo.getStoragePoolDetailedInfo().getId().toString())
                    .withNodeId(nodeId)
                    .withName(viprInfo.getStoragePoolDetailedInfo().getPoolName())
                    .withResponseTime(srmPoolInfo.getResponseTime())
                    .withUtilization(srmPoolInfo.getUtilization())
                    .withStorageSystemName(viprInfo.getStorageSystem().getName())
                    .withTotalIOPS(srmPoolInfo.getTotalIOPS())
                    .withUsedCapacity(srmPoolInfo.getUsedCapacity())
                    .build());
        }

        return new StoragePoolsInfo(storagePoolsPerformanceInfo, storagePoolsDetailedInfoMap, storageSystemsMap);
    }

    @Override
    public URI createSmartVirtualPool(final CreateSmartVirtualPoolRequest info) {
        synchronized (this) {
            if (viprClient.getVirtualPools().stream().anyMatch(pool -> Objects.equals(pool.getName(), info.getName()))
                    || mongoDao.findAll().stream().anyMatch(pool -> Objects.equals(pool.getName(), info.getName()))) {
                throw new IllegalArgumentException("Duplicate pool name: " + info.getName());
            }
            final URI id = viprClient.createVirtualPool(info.getName(), info.getStoragePoolIDList()
                    .stream()
                    .map(i -> {
                        try {
                            return new URI(i);
                        } catch (URISyntaxException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }).collect(Collectors.toList()));
            final VirtualPool virtualPool = getVirtualPool(info, id);
            mongoDao.save(virtualPool);
            return id;
        }
    }

    @Override
    public List<StoragePoolPerformanceInfo> getPoolsCharacteristicsUnderWorkload(
            final StoragePoolsInfo info, final ApplyWorkloadRequest workload) {
        return info.getStoragePoolsPerformanceInfo()
                .stream()
                .map(original -> {
                    final StorageSystemRestRep storageSystem = info.getStorageSystemsInfo().get(original.getId());
                    final StoragePoolRestRep storagePool = info.getStoragePoolsDetailedInfo().get(original.getId());
                    final VNXSizerRequest request = prepareRequest(original, storagePool, storageSystem, workload);

                    try {
                        final VNXSizerResponse response = vnxSizerclient.processSizerRequest(request);
                        return response.getErrorLevel() == CALCULATION_SUCCESS
                                ? getPoolInfoUnderWorkload(original, response)
                                : getPoolDoesNotFitWorkload(original);
                    } catch (Exception e) {
                        throw new IllegalStateException("Sizer engine fatal error. Error was:", e);
                        //replace with FunctionalInterface wrapper
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GetVirtualPoolsInfoResponse> getVirtualPools() {
        return mongoDao.findAll()
                .stream()
                .map(this::mapVirtualPoolModelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public Object provisionLun(final ProvisionLunRequest request) {
        final List<GetVirtualPoolsInfoResponse> pools = getVirtualPools();
        final GetVirtualPoolsInfoResponse targetPool = pools.stream()
                .filter(pool -> Objects.equals(pool.getId(), request.getVirtualPoolId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No suck pool " + request.getVirtualPoolId()));
        final StoragePoolsInfo storagePools = getStoragePoolsInfo();
        final StoragePoolPerformanceInfo suitablePool = storagePools.getStoragePoolsPerformanceInfo().stream()
                .filter(pool -> {
                    final StoragePoolRestRep storagePoolRestRep =
                            storagePools.getStoragePoolsDetailedInfo().get(pool.getId());
                    try {
                        final List<StoragePoolPerformanceInfo> info =
                                getPoolsCharacteristicsUnderWorkload(new StoragePoolsInfo(
                                                singletonList(pool), singletonMap(pool.getId(), storagePoolRestRep),
                                                storagePools.getStorageSystemsInfo()),
                                        new ApplyWorkloadRequest(targetPool.getApplicationsList()));
                        if (info.size() != 1) {
                            //noinspection ThrowCaughtLocally
                            throw new IllegalStateException("Expected one pool, found " + info.size());
                        }
                        final StoragePoolPerformanceInfo calculatedPool = info.get(0);
                        return calculatedPool.getResponseTime() + pool.getResponseTime()
                                <= targetPool.getTargetResponseTime()
                                || targetPool.getTargetResponseTime() == 0;
                    } catch (Exception e) {
                        LOGGER.error("Can't calculate pool load", e);
                        return false;
                    }
                })
                .min(Comparator.comparing(StoragePoolPerformanceInfo::getUtilization))
                .orElseThrow(() -> new IllegalArgumentException("No suitable pool found!"));
        try {
            viprClient.provisionLun(new URI(suitablePool.getId()), request.getCatacity());
            return null;
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid pool id " + suitablePool.getId(), e);
        }
    }

    private ViPRInfo getViPRInfo(final StoragePoolRestRep storagePool) {
        final StoragePoolRestRep storagePoolDetailedInfo = viprClient.getStoragePool(storagePool.getId());
        final StorageSystemRestRep storageSystem = viprClient.getStorageSystem(storagePool.getStorageSystem().getId());

        return new ViPRInfo(storagePoolDetailedInfo, storageSystem);
    }

    private SRMPoolInfo getSRMPoolInfo(final StoragePoolRestRep storagePool) {
        return srmClient.getStoragePoolInfoByName(storagePool.getPoolName());
    }

    private VNXSizerRequest prepareRequest(
            final StoragePoolPerformanceInfo poolPerformance,
            final StoragePoolRestRep storagePool,
            final StorageSystemRestRep storageSystem,
            final ApplyWorkloadRequest workload) {
        try {
            final VNXSizerRequest requestTemplateInstance = JSON.readValue(this.requestTemplate, VNXSizerRequest.class);

            final ApplicationsList applicationsList = populateApplicationsList(
                    poolPerformance, workload, requestTemplateInstance);
            final SystemPreferences systemPreferences = populateSystemPreferences(
                    storageSystem, requestTemplateInstance);
            final PoolInfo poolInfo = populatePoolInfo(storagePool, requestTemplateInstance);

            return new VNXSizerRequest(applicationsList, requestTemplateInstance.getSizerProjectType(),
                    poolInfo, systemPreferences);
        } catch (IOException e) {
            sneakyThrow(e);
            return null;
        }
    }

    private GetVirtualPoolsInfoResponse mapVirtualPoolModelToResponse(final VirtualPool pool) {
        final Optional<BlockVirtualPoolRestRep> virtualPoolRestRep = viprClient.getVirtualPools()
                .stream()
                .filter(i -> i.getName().equals(pool.getName()))
                .findFirst();

        final List<String> storagePools = virtualPoolRestRep.isPresent()
                ? virtualPoolRestRep
                .get()
                .getAssignedStoragePools()
                .stream()
                .map(i -> i.getId().toString())
                .collect(Collectors.toList())
                : null;

        return new GetVirtualPoolsInfoResponse(
                pool.getId(),
                pool.getName(),
                nodeId,
                pool.getTargetResponseTime(),
                storagePools,
                pool.getWorkload());
    }

    private static VirtualPool getVirtualPool(final CreateSmartVirtualPoolRequest info, final URI id) {
        return VirtualPoolBuilder.aVirtualPool()
                .withName(info.getName())
                .withTargetResponseTime(info.getTargetResponseTime())
                .withVpId(id.toString())
                .withWorkload(info.getApplicationsList())
                .build();
    }

    private static StoragePoolPerformanceInfo getPoolInfoUnderWorkload(
            final StoragePoolPerformanceInfo original, final VNXSizerResponse response) {
        final Pool calculatedPool = response.getResponseBody().get(0).getPools().get(0);

        return new StoragePoolPerformanceInfo(
                original.getId(),
                original.getNodeId(),
                original.getName(),
                calculatedPool.getPoolAvgResponceTime().doubleValue(),
                calculatedPool.getPoolDiskUtilization().doubleValue(),
                original.getStorageSystemName(),
                original.getTotalIOPS(),
                original.getUsedCapacity());
    }

    private static StoragePoolPerformanceInfo getPoolDoesNotFitWorkload(final StoragePoolPerformanceInfo original) {
        return new StoragePoolPerformanceInfo(
                original.getId(),
                original.getNodeId(),
                original.getName(),
                (double) Integer.MAX_VALUE,
                (double) Integer.MAX_VALUE,
                original.getStorageSystemName(),
                original.getTotalIOPS(),
                original.getUsedCapacity());
    }

    private static ApplicationsList populateApplicationsList(
            final StoragePoolPerformanceInfo poolPerformance,
            final ApplyWorkloadRequest workload,
            final VNXSizerRequest requestTemplateInstance) {
        final ApplicationsList applicationsList = requestTemplateInstance.getApplicationsList();
        applicationsList.getFreeFormWorkloads().addAll(workload.getApplicationsList().getFreeFormWorkloads());
        applicationsList.getOracleOLTPWorkloads().addAll(workload.getApplicationsList().getOracleOLTPWorkloads());

        final ApplicationWorkloads templateLoad = applicationsList.getFreeFormWorkloads().get(0);
        final ApplicationWorkloads existingLoad = new ApplicationWorkloads(
                templateLoad.getSkew(),
                templateLoad.getWriteSizeKb(),
                poolPerformance.getUsedCapacity().longValue(),
                templateLoad.getReadSizeKb(),
                templateLoad.getCacheHitReadPercent(),
                templateLoad.getRandomReadPercent(),
                templateLoad.getNumberOfLuns(),
                templateLoad.getRandomWritePercent(),
                templateLoad.getResponseTime(),
                templateLoad.getType(),
                templateLoad.getApplicationName(),
                templateLoad.getSequentialReadPercent(),
                templateLoad.getThinLun(),
                poolPerformance.getTotalIOPS().intValue(),
                templateLoad.getbThinLun(),
                templateLoad.getConcurrentProcesses(),
                templateLoad.getCacheHitWritePercent(),
                templateLoad.getSequentialWritePercent(),
                templateLoad.getDescription(),
                templateLoad.getId(),
                templateLoad.getFastCache(),
                templateLoad.getDeduplication());
        applicationsList.getFreeFormWorkloads().set(0, existingLoad);
        return applicationsList;
    }

    private static SystemPreferences populateSystemPreferences(
            final StorageSystemRestRep storageSystem,
            final VNXSizerRequest requestTemplateInstance) {
        final SystemPreferences templatePreferences = requestTemplateInstance.getSystemPreferences();
        return new SystemPreferences(
                SystemType.forValue(extractSystemType(storageSystem)),
                SystemFlareVersion.forValue(extractFlareVersion(storageSystem)),
                templatePreferences.getDaeStrategy(),
                templatePreferences.getUnified(),
                templatePreferences.getCompactSystemRequested(),
                templatePreferences.getDoBestFit(),
                templatePreferences.getDataMovers(),
                templatePreferences.getSystemUtilization(),
                templatePreferences.getSystemFlareVersionVnxE(),
                templatePreferences.getDataDecaySystemRange(),
                templatePreferences.getDataDecayMonoliticStripeWidth());
    }

    private static PoolInfo populatePoolInfo(
            final StoragePoolRestRep storagePool,
            final VNXSizerRequest requestTemplateInstance) {
        final PoolInfo templateInfo = requestTemplateInstance.getPoolInfo();
        return new PoolInfo(
                templateInfo.getDriveCount(),
                templateInfo.getPoolType(),
                extractDiskType(storagePool),
                templateInfo.getDriveSize(),
                templateInfo.getDriveFormFactor(),
                TierRaidType.forValue(extractRAIDLevel(storagePool)),
                templateInfo.getStripeWidth(),
                templateInfo.getHotSpares());
    }

    private static String extractSystemType(final StorageSystemRestRep storageSystem) {
        final String[] systemTypeArray = storageSystem.getModel().split(" ");
        return SS.matcher(systemTypeArray[systemTypeArray.length - 1])
                .replaceAll(Matcher.quoteReplacement("5400"));
    }

    private static String extractFlareVersion(final StorageSystemRestRep storageSystem) {
        final StringTokenizer stringTokenizer = new StringTokenizer(storageSystem.getFirmwareVersion());
        stringTokenizer.nextToken(".");
        return "OE" + ' ' + stringTokenizer.nextToken(".");
    }

    private static String extractDiskType(final StoragePoolRestRep storagePool) {
        return "DISK_PERF_TYPE_" + storagePool.getDriveTypes().iterator().next() + "10K";
    }

    private static String extractRAIDLevel(final StoragePoolRestRep storagePool) {
        final String level = storagePool.getRaidLevels().iterator().next();
        return level.substring(0, 4) + "_TYPE_" + level.substring(4);
    }

    private static class ViPRInfo {
        private final StoragePoolRestRep storagePoolDetailedInfo;
        private final StorageSystemRestRep storageSystem;

        ViPRInfo(final StoragePoolRestRep storagePoolDetailedInfo,
                 final StorageSystemRestRep storageSystem) {
            this.storagePoolDetailedInfo = storagePoolDetailedInfo;
            this.storageSystem = storageSystem;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("storagePoolDetailedInfo", storagePoolDetailedInfo)
                    .add("storageSystem", storageSystem)
                    .toString();
        }

        StoragePoolRestRep getStoragePoolDetailedInfo() {
            return storagePoolDetailedInfo;
        }

        StorageSystemRestRep getStorageSystem() {
            return storageSystem;
        }
    }
}
