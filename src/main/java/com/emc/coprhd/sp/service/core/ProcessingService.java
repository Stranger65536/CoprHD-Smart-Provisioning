/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.transfer.client.request.ApplyWorkloadRequest;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.coprhd.sp.transfer.client.response.StoragePoolPerformanceInfo;

import java.net.URI;
import java.util.List;

public interface ProcessingService {
    StoragePoolsInfo getStoragePoolsInfo();

    URI createSmartVirtualPool(final CreateSmartVirtualPoolRequest info);

    List<StoragePoolPerformanceInfo> getPoolsCharacteristicsUnderWorkload(
            final StoragePoolsInfo info,
            final ApplyWorkloadRequest workload);

    List<GetVirtualPoolsInfoResponse> getVirtualPools();
}
