/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.controller.ContextPaths.PoolManager;
import com.emc.coprhd.sp.controller.ContextPaths.PoolManager.Workload;
import com.emc.coprhd.sp.controller.ContextPaths.ServiceCatalog;
import com.emc.coprhd.sp.controller.ContextPaths.StoragePools;
import com.emc.coprhd.sp.controller.ContextPaths.VirtualPools;
import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.service.core.ClusterStateService;
import com.emc.coprhd.sp.service.core.ProcessingService;
import com.emc.coprhd.sp.service.core.RemoteNodeExecutor;
import com.emc.coprhd.sp.transfer.client.request.ApplyWorkloadRequest;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.coprhd.sp.util.RuntimeUtils;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.emc.coprhd.sp.controller.ContextPaths.NODE;
import static com.emc.coprhd.sp.controller.ContextPaths.NODE_ID;

@Controller
public class PoolManagerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolManagerController.class);

    private final ClusterStateService clusterStateService;
    private final ProcessingService processingService;
    private final RemoteNodeExecutor remoteNodeExecutor;

    @Autowired
    public PoolManagerController(
            final ClusterStateService clusterStateService,
            final ProcessingService processingService,
            final RemoteNodeExecutor remoteNodeExecutor) {
        this.clusterStateService = clusterStateService;
        this.processingService = processingService;
        this.remoteNodeExecutor = remoteNodeExecutor;
    }

    @SuppressWarnings("SameReturnValue")
    @GetMapping(PoolManager.ROOT)
    public String handleLoadPageRequest() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        return PoolManager.ROOT;
    }

    @SuppressWarnings("SameReturnValue")
    @GetMapping(ServiceCatalog.ROOT)
    public String handleServiceLoadPageRequest() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        return ServiceCatalog.ROOT;
    }

    @GetMapping(StoragePools.ROOT)
    public ResponseEntity<List<StoragePoolPerformanceInfo>> getStoragePools() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final List<StoragePoolPerformanceInfo> result = clusterStateService.getAvailableNodes().stream()
                .map(node -> {
                    try {
                        return remoteNodeExecutor.getStoragePools(node);
                    } catch (RuntimeException e) {
                        LOGGER.error("Can't obtain storage pools from node {}", node, e);
                        return Collections.<StoragePoolPerformanceInfo>emptyList();
                    }
                }).flatMap(List::stream)
                .collect(Collectors.toList());
        LOGGER.debug("{} pools: {}", RuntimeUtils.exitMethodMessage(), result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(StoragePools.ROOT + NODE_ID + StoragePools.POOL_ID)
    public ResponseEntity<?> getStoragePoolInfo(
            @PathVariable(NODE) final String nodeId,
            @PathVariable(StoragePools.ID) final String id) {
        LOGGER.debug("{}, node: {}, poolId: {}", RuntimeUtils.enterMethodMessage(), nodeId, id);
        final ClusterNode node = clusterStateService.getAvailableNodes().stream()
                .filter(n -> Objects.equals(n.getId(), nodeId))
                .findFirst().orElse(null);

        if (node == null) {
            LOGGER.error("No node with id {} found!", id);
            return ResponseEntity.notFound().build();
        }

        try {
            final StoragePoolRestRep pool = remoteNodeExecutor.getStoragePoolInfo(id, node);
            LOGGER.debug("{}, node: {}, poolId: {}, pool: {}", RuntimeUtils.exitMethodMessage(), nodeId, id, pool);
            return ResponseEntity.ok(pool);
        } catch (RuntimeException e) {
            LOGGER.error("Can't obtain storage pool {} info from node {}", id, node, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(PoolManager.ROOT + Workload.ROOT)
    public ResponseEntity<List<StoragePoolPerformanceInfo>> applyWorkloadInformationRequest(
            @RequestBody final ApplyWorkloadRequest request) {
        LOGGER.debug("{} request: {}", RuntimeUtils.enterMethodMessage(), request);
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
        final List<StoragePoolPerformanceInfo> result =
                processingService.getPoolsCharacteristicsUnderWorkload(info, request);
        LOGGER.debug("{} request: {}, response: {}", RuntimeUtils.exitMethodMessage(), request, result);
        return ResponseEntity.ok(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(VirtualPools.ROOT)
    public ResponseEntity<URI> createVirtualPool(@RequestBody final CreateSmartVirtualPoolRequest request) {
        LOGGER.debug("{} request: {}", RuntimeUtils.enterMethodMessage(), request);
        final ClusterNode node = clusterStateService.getAvailableNodes().stream()
                .filter(n -> Objects.equals(n.getId(), request.getNodeId()))
                .findFirst().orElse(null);

        if (node == null) {
            LOGGER.error("No node with id {} found!", request.getNodeId());
            return ResponseEntity.notFound().build();
        }

        try {
            final StoragePoolsInfo info = processingService.getStoragePoolsInfo();

            if (request.getStoragePoolIDList().stream()
                    .allMatch(i -> info.getStoragePoolsDetailedInfo().containsKey(i))
                    && info.getStoragePoolsPerformanceInfo().stream()
                    .filter(i -> Objects.equals(i.getNodeId(), node.getId()))
                    .map(StoragePoolPerformanceInfo::getId)
                    .collect(Collectors.toList())
                    .containsAll(request.getStoragePoolIDList())) {
                processingService.createSmartVirtualPool(request);
                final URI poolURI = remoteNodeExecutor.createVirtualPool(request, node);
                LOGGER.debug("{} request: {}, poolURI: {}", RuntimeUtils.exitMethodMessage(), request, poolURI);
                return ResponseEntity.ok(poolURI);
            } else {
                LOGGER.error("Missing storage pool ids or not located in one node: {}, all: {}",
                        request.getStoragePoolIDList(), info.getStoragePoolsPerformanceInfo());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (RuntimeException e) {
            LOGGER.error("Can't create virtual pool {} on node {}", request, node, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(VirtualPools.ROOT)
    public ResponseEntity<List<GetVirtualPoolsInfoResponse>> handleGetVirtualPoolsListRequest() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final List<GetVirtualPoolsInfoResponse> result = clusterStateService.getAvailableNodes().stream()
                .map(node -> {
                    try {
                        return remoteNodeExecutor.getVirtualPools(node);
                    } catch (RuntimeException e) {
                        LOGGER.error("Can't obtain storage pools from node {}", node, e);
                        return Collections.<GetVirtualPoolsInfoResponse>emptyList();
                    }
                }).flatMap(List::stream)
                .collect(Collectors.toList());
        LOGGER.debug("{} pools: {}", RuntimeUtils.exitMethodMessage(), result);
        return ResponseEntity.ok(result);
    }
}