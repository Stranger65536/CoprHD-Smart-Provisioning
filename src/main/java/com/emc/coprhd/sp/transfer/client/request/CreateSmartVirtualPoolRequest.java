/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.client.request;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class CreateSmartVirtualPoolRequest {
    private static final String NAME = "name";
    private static final String NODE_ID = "nodeId";
    private static final String TARGET_RESPONSE_TIME = "targetResponseTime";
    private static final String STORAGE_POOLS = "storagePools";
    private static final String APPLICATIONS_LIST = "applicationsList";

    private final String name;
    private final String nodeId;
    private final Double targetResponseTime;
    private final List<String> storagePoolIDList;
    private final ApplicationsList applicationsList;

    @JsonCreator
    public CreateSmartVirtualPoolRequest(
            @JsonProperty(NAME) final String name,
            @JsonProperty(NODE_ID) final String nodeId,
            @JsonProperty(TARGET_RESPONSE_TIME) final Double targetResponseTime,
            @JsonProperty(STORAGE_POOLS) final List<String> storagePoolIDList,
            @JsonProperty(APPLICATIONS_LIST) final ApplicationsList applicationsList) {
        this.name = name;
        this.nodeId = nodeId;
        this.targetResponseTime = targetResponseTime;
        this.storagePoolIDList = storagePoolIDList;
        this.applicationsList = applicationsList;
    }

    public String getName() {
        return name;
    }

    public String getNodeId() {
        return nodeId;
    }

    public Double getTargetResponseTime() {
        return targetResponseTime;
    }

    public List<String> getStoragePoolIDList() {
        return storagePoolIDList;
    }

    public ApplicationsList getApplicationsList() {
        return applicationsList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("nodeId", nodeId)
                .add("targetResponseTime", targetResponseTime)
                .add("storagePoolIDList", storagePoolIDList)
                .add("applicationsList", applicationsList)
                .toString();
    }
}