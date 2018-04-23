/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.srm;

import com.emc.coprhd.sp.service.common.FallBackable;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;

public interface SRMClient extends FallBackable {
    SRMPoolInfo getStoragePoolInfoByName(final String poolName);
}
