/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.repository;

@SuppressWarnings("ALL")
public interface DaoConstants {
    interface VirtualPools {
        String COLLECTION_NAME = "virtual-pools";
        String ID = "vp_id";
        String NAME = "name";
        String TARGET_RESPONSE_TIME = "target-response-time";
        String WORKLOAD = "workload";
    }
}
