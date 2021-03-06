/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.model;

import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StoragePoolsInfo {
    private static final String STORAGE_POOLS_PERFORMANCE_INFO = "storagePoolsPerformanceInfo";
    private static final String STORAGE_POOLS_DETAILED_INFO = "storagePoolsDetailedInfo";
    private static final String STORAGE_SYSTEMS_INFO = "storageSystemsInfo";

    private final List<StoragePoolPerformanceInfo> storagePoolsPerformanceInfo;
    private final Map<String, StoragePoolRestRep> storagePoolsDetailedInfo;
    private final Map<String, StorageSystemRestRep> storageSystemsInfo;

    @JsonCreator
    public StoragePoolsInfo(
            @JsonProperty(STORAGE_POOLS_PERFORMANCE_INFO) final List<StoragePoolPerformanceInfo> storagePoolsPerformanceInfo,
            @JsonProperty(STORAGE_POOLS_DETAILED_INFO) final Map<String, StoragePoolRestRep> storagePoolsDetailedInfo,
            @JsonProperty(STORAGE_SYSTEMS_INFO) final Map<String, StorageSystemRestRep> storageSystemsInfo) {
        this.storagePoolsPerformanceInfo = Collections.unmodifiableList(storagePoolsPerformanceInfo);
        this.storagePoolsDetailedInfo = Collections.unmodifiableMap(storagePoolsDetailedInfo);
        this.storageSystemsInfo = Collections.unmodifiableMap(storageSystemsInfo);
    }

    @JsonGetter(STORAGE_POOLS_PERFORMANCE_INFO)
    public List<StoragePoolPerformanceInfo> getStoragePoolsPerformanceInfo() {
        return storagePoolsPerformanceInfo;
    }

    @JsonGetter(STORAGE_POOLS_DETAILED_INFO)
    public Map<String, StoragePoolRestRep> getStoragePoolsDetailedInfo() {
        return storagePoolsDetailedInfo;
    }

    @JsonGetter(STORAGE_SYSTEMS_INFO)
    public Map<String, StorageSystemRestRep> getStorageSystemsInfo() {
        return storageSystemsInfo;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("storagePoolsPerformanceInfo", storagePoolsPerformanceInfo)
                .add("storagePoolsDetailedInfo", storagePoolsDetailedInfo)
                .add("storageSystemsInfo", storageSystemsInfo)
                .toString();
    }
}
