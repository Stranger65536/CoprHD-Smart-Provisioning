package com.emc.coprhd.sp.transfer.client.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class ProvisionLunRequest {
    private static final String NAME = "name";
    private static final String VP_ID = "virtualPoolId";
    private static final String NODE_ID = "nodeId";
    private static final String TARGET_RESPONSE_TIME = "targetResponseTime";

    private final String name;
    private final String virtualPoolId;
    private final String nodeId;
    private final long catacity;

    @JsonCreator
    public ProvisionLunRequest(
            @JsonProperty(NAME) final String name,
            @JsonProperty(VP_ID) final String virtualPoolId,
            @JsonProperty(NODE_ID) final String nodeId,
            @JsonProperty(TARGET_RESPONSE_TIME) final long catacity) {
        this.name = name;
        this.virtualPoolId = virtualPoolId;
        this.nodeId = nodeId;
        this.catacity = catacity;
    }

    public String getName() {
        return name;
    }

    public String getVirtualPoolId() {
        return virtualPoolId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public long getCatacity() {
        return catacity;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("virtualPoolId", virtualPoolId)
                .add("nodeId", nodeId)
                .add("catacity", catacity)
                .toString();
    }
}
