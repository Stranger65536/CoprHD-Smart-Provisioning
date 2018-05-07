package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.controller.ContextPaths.StoragePools;
import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.coprhd.sp.util.RuntimeUtils;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.emc.coprhd.sp.controller.ContextPaths.DIST;

@Service
public class RemoteNodeExecutorImpl implements RemoteNodeExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNodeExecutorImpl.class);

    private final String nodeId;
    private final RestOperations restTemplate = new RestTemplate();
    private final ClusterStateService clusterStateService;
    private final ProcessingService processingService;

    @Autowired
    public RemoteNodeExecutorImpl(
            @Value("${com.emc.coprhd.sp.node-id}") final String nodeId,
            final ClusterStateService clusterStateService,
            final ProcessingService processingService) {
        this.nodeId = nodeId;
        this.clusterStateService = clusterStateService;
        this.processingService = processingService;
    }

    @Override
    public List<StoragePoolPerformanceInfo> getStoragePools(final ClusterNode clusterNode) {
        LOGGER.debug("{} node: {}", RuntimeUtils.enterMethodMessage(), clusterNode);
        if (nodeId.equals(clusterNode.getId())) {
            final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
            LOGGER.debug("{} LOCAL Storage Pools retrieve node: {} pools: {}",
                    RuntimeUtils.exitMethodMessage(), clusterNode, info.getStoragePoolsPerformanceInfo());
            return info.getStoragePoolsPerformanceInfo();
        } else {
            final String url = "http://" + clusterStateService.getNodeAddress(clusterNode) + DIST + StoragePools.ROOT;
            LOGGER.debug("GET > {}", url);
            final ResponseEntity<StoragePoolPerformanceInfo[]> responseEntity =
                    restTemplate.getForEntity(url, StoragePoolPerformanceInfo[].class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                LOGGER.error("GET < {} {}", url, responseEntity);
                throw new RestClientException("Error fetching storage pools list from " + clusterNode);
            }

            LOGGER.debug("GET < 200 OK {}", url);

            final List<StoragePoolPerformanceInfo> result =
                    Arrays.stream(responseEntity.getBody()).collect(Collectors.toList());

            LOGGER.debug("{} REMOTE Storage Pools retrieve node: {} pools: {}",
                    RuntimeUtils.exitMethodMessage(), clusterNode, result);
            return result;
        }
    }

    @Override
    public StoragePoolRestRep getStoragePoolInfo(final String id, final ClusterNode clusterNode) {
        LOGGER.debug("{} node: {}, id: {}", RuntimeUtils.enterMethodMessage(), clusterNode, id);
        if (nodeId.equals(clusterNode.getId())) {
            final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
            final StoragePoolRestRep result = info.getStoragePoolsDetailedInfo().get(id);
            LOGGER.debug("{} LOCAL Storage Pool retrieve node: {} id: {} pool: {}",
                    RuntimeUtils.exitMethodMessage(), clusterNode, id, result);
            return result;
        } else {
            final String url = "http://" + clusterStateService.getNodeAddress(clusterNode)
                    + DIST + StoragePools.ROOT + '/' + id;
            LOGGER.debug("GET > {}", url);
            final ResponseEntity<StoragePoolRestRep> responseEntity =
                    restTemplate.getForEntity(url, StoragePoolRestRep.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                LOGGER.error("GET < {} {}", url, responseEntity);
                throw new RestClientException("Error fetching storage pools list from " + clusterNode);
            }

            LOGGER.debug("GET < 200 OK {}", url);
            LOGGER.debug("{} REMOTE Storage Pool retrieve node: {} id: {} pool: {}",
                    RuntimeUtils.exitMethodMessage(), clusterNode, id, responseEntity.getBody());
            return responseEntity.getBody();
        }
    }

    @Override
    public URI createVirtualPool(final CreateSmartVirtualPoolRequest request, final ClusterNode clusterNode) {
        return null;
    }

    @Override
    public List<GetVirtualPoolsInfoResponse> getVirtualPools(final ClusterNode clusterNode) {
        return null;
    }
}