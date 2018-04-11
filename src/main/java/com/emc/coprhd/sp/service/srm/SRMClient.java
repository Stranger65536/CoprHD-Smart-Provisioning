/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.srm;

import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;

@FunctionalInterface
public interface SRMClient {
    SRMPoolInfo getStoragePoolInfoByName(final String poolName);
}
