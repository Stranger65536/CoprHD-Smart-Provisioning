/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

@SuppressWarnings("ALL")
public interface ContextPaths {
    String ROOT = "/";
    String ERROR = "/error";
    String DIST = "/dist";
    String NODE = "node";
    String NODE_ID = "/{" + NODE + '}';

    interface PoolManager {
        String ROOT = "/pool-manager";

        interface Workload {
            String ROOT = "/workload";
        }
    }

    interface StoragePools {
        String ROOT = "/storage-pools";
        String ID = "id";
        String POOL_ID = "/{" + ID + '}';
    }

    interface VirtualPools {
        String ROOT = "/virtual-pools";
    }
}
