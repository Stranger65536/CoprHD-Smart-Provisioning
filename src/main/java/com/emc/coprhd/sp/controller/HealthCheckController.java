/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.dao.mongo.VirtualPool;
import com.emc.coprhd.sp.dao.mongo.VirtualPool.VirtualPoolBuilder;
import com.emc.coprhd.sp.dto.HealthCheckResponse;
import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.repository.mongo.MongoDao;
import com.emc.coprhd.sp.service.core.ClusterStateService;
import com.emc.coprhd.sp.service.sizer.VNXSizerClient;
import com.emc.coprhd.sp.service.srm.SRMClient;
import com.emc.coprhd.sp.service.vipr.ViPRClient;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;
import com.emc.coprhd.sp.transfer.vnx.sizer.request.VNXSizerRequest;
import com.emc.coprhd.sp.transfer.vnx.sizer.response.VNXSizerResponse;
import com.emc.coprhd.sp.util.RuntimeUtils;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HealthCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

    private final MongoDao mongoDao;
    private final SRMClient srmClient;
    private final ViPRClient viprClient;
    private final ObjectMapper objectMapper;
    private final VNXSizerClient vnxSizerClient;
    private final ClusterStateService clusterStateService;
    private final String vnxSizerTemplateFile;

    @Autowired
    public HealthCheckController(
            final MongoDao mongoDao,
            final SRMClient srmClient,
            final ViPRClient viprClient,
            final ObjectMapper objectMapper,
            final VNXSizerClient vnxSizerClient,
            final ClusterStateService clusterStateService,
            @Value("${com.emc.coprhd.sp.sizer.template}") final String vnxSizerTemplateFile) {
        this.mongoDao = mongoDao;
        this.srmClient = srmClient;
        this.viprClient = viprClient;
        this.objectMapper = objectMapper;
        this.vnxSizerClient = vnxSizerClient;
        this.clusterStateService = clusterStateService;
        this.vnxSizerTemplateFile = vnxSizerTemplateFile;
    }

    @ResponseBody
    @GetMapping(value = "/health", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse healthCheck() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final HealthCheckResponse response = new HealthCheckResponse("ok");
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/mongo", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse testMongo() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final VirtualPool pool = VirtualPoolBuilder.aVirtualPool().withName("yep").build();
        mongoDao.insert(pool);
        final String id = pool.getId();
        final VirtualPool foundPool = mongoDao.findById(id);
        checkNotNull(foundPool);
        checkArgument("yep".equals(foundPool.getName()));
        mongoDao.delete(pool);
        final HealthCheckResponse response = new HealthCheckResponse("ok");
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/hz", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse testHz() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final List<ClusterNode> nodes = clusterStateService.getAvailableNodes();
        final Map<String, String> addresses = nodes.stream()
                .map(node -> Pair.of(node.getId(), clusterStateService.getNodeAddress(node)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        final HealthCheckResponse response = new HealthCheckResponse("ok",
                ImmutableMap.of("nodes", nodes, "addresses", addresses));
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/srm", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse testSrm() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final SRMPoolInfo info = srmClient.getStoragePoolInfoByName("YEP");
        final HealthCheckResponse response = new HealthCheckResponse("ok", info);
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/sizer", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse testSizer() throws IOException {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final File requestTemplateFile = new File(checkNotNull(vnxSizerTemplateFile,
                "JSON template path must be specified!"));
        checkState(requestTemplateFile.exists(), "JSON template file must exist!");
        final VNXSizerRequest request = objectMapper.readValue(requestTemplateFile, VNXSizerRequest.class);
        final VNXSizerResponse clientResponse = vnxSizerClient.processSizerRequest(request);
        final HealthCheckResponse response = new HealthCheckResponse("ok", clientResponse);
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/vipr", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    public HealthCheckResponse testVipr() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final Collection<StoragePoolRestRep> info = viprClient.getStoragePools();
        final HealthCheckResponse response = new HealthCheckResponse("ok", info);
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }
}
