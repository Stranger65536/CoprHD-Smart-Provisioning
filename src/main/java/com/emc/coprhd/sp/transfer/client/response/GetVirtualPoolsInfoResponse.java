/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.client.response;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class GetVirtualPoolsInfoResponse {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NODE_ID = "nodeId";
    private static final String TARGET_RESPONSE_TIME = "targetResponseTime";
    private static final String STORAGE_POOLS = "storagePools";
    private static final String APPLICATIONS_LIST = "applicationsList";

    @JsonProperty(ID)
    private final String id;
    @JsonProperty(NAME)
    private final String name;
    @JsonProperty(NODE_ID)
    private final String nodeId;
    @JsonProperty(TARGET_RESPONSE_TIME)
    private final Double targetResponseTime;
    @JsonProperty(STORAGE_POOLS)
    private final List<String> storagePoolIDList;
    @JsonProperty(APPLICATIONS_LIST)
    private final ApplicationsList applicationsList;

    @JsonCreator
    public GetVirtualPoolsInfoResponse(
            @JsonProperty(ID) final String id,
            @JsonProperty(NAME) final String name,
            @JsonProperty(NODE_ID) final String nodeId,
            @JsonProperty(TARGET_RESPONSE_TIME) final Double targetResponseTime,
            @JsonProperty(STORAGE_POOLS) final List<String> storagePoolIDList,
            @JsonProperty(APPLICATIONS_LIST) final ApplicationsList applicationsList) {
        this.id = id;
        this.name = name;
        this.nodeId = nodeId;
        this.targetResponseTime = targetResponseTime;
        this.storagePoolIDList = storagePoolIDList;
        this.applicationsList = applicationsList;
    }

    public String getId() {
        return id;
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
                .add("id", id)
                .add("name", name)
                .add("nodeId", nodeId)
                .add("targetResponseTime", targetResponseTime)
                .add("storagePoolIDList", storagePoolIDList)
                .add("applicationsList", applicationsList)
                .toString();
    }
}
