/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContextProperties {
    private final String virtualPoolsCollectionName;

    @Autowired
    public ContextProperties(
            @Value("${com.emc.coprhd.sp.mongo.vp-collection-name}") final String virtualPoolsCollectionName) {
        this.virtualPoolsCollectionName = virtualPoolsCollectionName;
    }

    public String getVirtualPoolsCollectionName() {
        return virtualPoolsCollectionName;
    }
}
