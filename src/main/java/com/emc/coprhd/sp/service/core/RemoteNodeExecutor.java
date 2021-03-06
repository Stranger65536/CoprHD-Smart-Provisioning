/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.model.StoragePoolsInfo;
import com.emc.coprhd.sp.transfer.client.request.CreateSmartVirtualPoolRequest;
import com.emc.coprhd.sp.transfer.client.request.ProvisionLunRequest;
import com.emc.coprhd.sp.transfer.client.response.GetVirtualPoolsInfoResponse;
import com.emc.storageos.model.pools.StoragePoolRestRep;

import java.net.URI;
import java.util.List;

public interface RemoteNodeExecutor {
    StoragePoolsInfo getStoragePools(final ClusterNode clusterNode);

    StoragePoolRestRep getStoragePoolInfo(final String id, final ClusterNode clusterNode);

    URI createVirtualPool(final CreateSmartVirtualPoolRequest request, final ClusterNode clusterNode);

    List<GetVirtualPoolsInfoResponse> getVirtualPools(final ClusterNode clusterNode);

    void provisionLun(ProvisionLunRequest request, ClusterNode clusterNode);
}
