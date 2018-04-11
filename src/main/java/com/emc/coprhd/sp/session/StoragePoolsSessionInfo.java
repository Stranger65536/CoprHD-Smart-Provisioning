/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.session;

import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StoragePoolsSessionInfo {
    private final List<StoragePoolPerformanceInfo> storagePoolsPerformanceInfo;
    private final Map<String, StoragePoolRestRep> storagePoolsDetailedInfo;
    private final Map<String, StorageSystemRestRep> storageSystemsInfo;

    public StoragePoolsSessionInfo(
            final List<StoragePoolPerformanceInfo> storagePoolsPerformanceInfo,
            final Map<String, StoragePoolRestRep> storagePoolsDetailedInfo,
            final Map<String, StorageSystemRestRep> storageSystemsInfo) {
        this.storagePoolsPerformanceInfo = Collections.unmodifiableList(storagePoolsPerformanceInfo);
        this.storagePoolsDetailedInfo = Collections.unmodifiableMap(storagePoolsDetailedInfo);
        this.storageSystemsInfo = Collections.unmodifiableMap(storageSystemsInfo);
    }

    public List<StoragePoolPerformanceInfo> getStoragePoolsPerformanceInfo() {
        return storagePoolsPerformanceInfo;
    }

    public Map<String, StoragePoolRestRep> getStoragePoolsDetailedInfo() {
        return storagePoolsDetailedInfo;
    }

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
