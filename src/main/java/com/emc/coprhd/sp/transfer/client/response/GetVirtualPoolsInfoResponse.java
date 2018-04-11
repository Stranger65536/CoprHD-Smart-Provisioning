/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.client.response;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class GetVirtualPoolsInfoResponse {
    private static final String NAME = "name";
    private static final String TARGET_RESPONSE_TIME = "targetResponseTime";
    private static final String STORAGE_POOLS = "storagePools";
    private static final String APPLICATIONS_LIST = "applicationsList";

    @JsonProperty(NAME)
    private final String name;
    @JsonProperty(TARGET_RESPONSE_TIME)
    private final Double targetResponseTime;
    @JsonProperty(STORAGE_POOLS)
    private final List<String> storagePoolIDList;
    @JsonProperty(APPLICATIONS_LIST)
    private final ApplicationsList applicationsList;

    public GetVirtualPoolsInfoResponse(
            final String name,
            final Double targetResponseTime,
            final List<String> storagePoolIDList,
            final ApplicationsList applicationsList) {
        this.name = name;
        this.targetResponseTime = targetResponseTime;
        this.storagePoolIDList = storagePoolIDList;
        this.applicationsList = applicationsList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("targetResponseTime", targetResponseTime)
                .add("storagePoolIDList", storagePoolIDList)
                .add("applicationsList", applicationsList)
                .toString();
    }
}
