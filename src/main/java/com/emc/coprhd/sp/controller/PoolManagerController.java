/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.controller.ContextPaths.PoolManager;
import com.emc.coprhd.sp.controller.ContextPaths.PoolManager.Workload;
import com.emc.coprhd.sp.controller.ContextPaths.StoragePools;
import com.emc.coprhd.sp.controller.ContextPaths.VirtualPools;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.service.core.ProcessingService;
import com.emc.coprhd.sp.transfer.client.request.ApplyWorkloadRequest;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

import static com.hazelcast.util.ExceptionUtil.sneakyThrow;

@Controller
public class PoolManagerController {
    private static final ObjectMapper JSON = new ObjectMapper();

    private final ProcessingService processingService;

    @Autowired
    public PoolManagerController(final ProcessingService processingService) {
        this.processingService = processingService;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = PoolManager.ROOT, method = RequestMethod.GET)
    public String handleLoadPageRequest() {
        return PoolManager.ROOT;
    }

    @RequestMapping(value = StoragePools.ROOT, method = RequestMethod.GET)
    public ResponseEntity<List<StoragePoolPerformanceInfo>> getStoragePools() {
        return ResponseEntity.ok(getStoragePoolsPerformanceInfo());
    }

    @RequestMapping(value = StoragePools.ROOT + StoragePools.POOL_ID, method = RequestMethod.GET)
    public ResponseEntity<?> getStoragePoolInfo(@PathVariable(StoragePools.ID) final String id) {
        final StoragePoolRestRep info = getStoragePoolDetailedInfo(id);
        return info == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(info);
    }

    @RequestMapping(value = PoolManager.ROOT + Workload.ROOT, method = RequestMethod.POST)
    public ResponseEntity<List<StoragePoolPerformanceInfo>> handleApplyWorkloadInformationRequest(
            @RequestBody final String request) {
        try {
            final ApplyWorkloadRequest requestObject = JSON.readValue(request, ApplyWorkloadRequest.class);
            final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
            return ResponseEntity.ok(processingService.getPoolsCharacteristicsUnderWorkload(info, requestObject));
        } catch (IOException e) {
            sneakyThrow(e);
            //noinspection ReturnOfNull
            return null;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = VirtualPools.ROOT, method = RequestMethod.POST)
    public ResponseEntity<Void> handleCreateVirtualPoolRequest(@RequestBody final String request) {
        try {
            final CreateSmartVirtualPoolRequest requestObject =
                    JSON.readValue(request, CreateSmartVirtualPoolRequest.class);
            final StoragePoolsInfo info = processingService.getStoragePoolsInfo();

            if (requestObject.getStoragePoolIDList().stream().allMatch(i ->
                    info.getStoragePoolsDetailedInfo().containsKey(i))) {
                processingService.createSmartVirtualPool(requestObject);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (IOException e) {
            sneakyThrow(e);
            //noinspection ReturnOfNull
            return null;
        }
    }

    @RequestMapping(value = VirtualPools.ROOT, method = RequestMethod.GET)
    public ResponseEntity<List<GetVirtualPoolsInfoResponse>> handleGetVirtualPoolsListRequest() {
        return new ResponseEntity<>(processingService.getVirtualPools(), HttpStatus.OK);
    }

    private List<StoragePoolPerformanceInfo> getStoragePoolsPerformanceInfo() {
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
        return info.getStoragePoolsPerformanceInfo();
    }

    private StoragePoolRestRep getStoragePoolDetailedInfo(@PathVariable(StoragePools.ID) final String id) {
        final StoragePoolsInfo info = processingService.getStoragePoolsInfo();
        return info.getStoragePoolsDetailedInfo().get(id);
    }
}