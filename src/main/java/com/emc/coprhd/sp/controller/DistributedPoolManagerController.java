/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.controller.ContextPaths.StoragePools;
import com.emc.coprhd.sp.controller.ContextPaths.VirtualPools;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.service.core.ProcessingService;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

import static com.emc.coprhd.sp.controller.ContextPaths.DIST;

@Controller
public class DistributedPoolManagerController {
    private final ProcessingService processingService;

    @Autowired
    public DistributedPoolManagerController(final ProcessingService processingService) {
        this.processingService = processingService;
    }

    @GetMapping(DIST + StoragePools.ROOT)
    public ResponseEntity<List<StoragePoolPerformanceInfo>> getStoragePools() {
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
        return ResponseEntity.ok(info.getStoragePoolsPerformanceInfo());
    }

    @GetMapping(DIST + StoragePools.ROOT + StoragePools.POOL_ID)
    public ResponseEntity<StoragePoolRestRep> getStoragePoolInfo(@PathVariable(StoragePools.ID) final String id) {
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
        final StoragePoolRestRep result = info.getStoragePoolsDetailedInfo().get(id);
        return result == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(DIST + VirtualPools.ROOT)
    public ResponseEntity<?> handleCreateVirtualPoolRequest(
            @RequestBody final CreateSmartVirtualPoolRequest request) {
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();

        if (request.getStoragePoolIDList().stream().allMatch(i ->
                info.getStoragePoolsDetailedInfo().containsKey(i))) {
            processingService.createSmartVirtualPool(request);
            return ResponseEntity.ok().build();
        } else {
            final Map<String, Object> data = ImmutableMap.of(
                    "availablePools", info.getStoragePoolsDetailedInfo(),
                    "requestedPools", request.getStoragePoolIDList());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(data);
        }
    }

    @GetMapping(DIST + VirtualPools.ROOT)
    public ResponseEntity<List<GetVirtualPoolsInfoResponse>> handleGetVirtualPoolsListRequest() {
        return new ResponseEntity<>(processingService.getVirtualPools(), HttpStatus.OK);
    }
}