/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class StoragePoolPerformanceInfo {
    private static final String ID = "id";
    private static final String NODE_ID = "nodeId";
    private static final String NAME = "name";
    private static final String RESPONSE_TIME = "responseTime";
    private static final String UTILIZATION = "utilization";
    private static final String STORAGE_SYSTEM_NAME = "storageSystem";
    private static final String IOPS = "iops";
    private static final String USED_CAPACITY = "usedCapacity";

    @JsonProperty(ID)
    private final String id;
    @JsonProperty(NODE_ID)
    private final String nodeId;
    @JsonProperty(NAME)
    private final String name;
    @JsonProperty(RESPONSE_TIME)
    private final Double responseTime;
    @JsonProperty(UTILIZATION)
    private final Double utilization;
    @JsonProperty(STORAGE_SYSTEM_NAME)
    private final String storageSystemName;
    @JsonProperty(IOPS)
    private final Double totalIOPS;
    @JsonProperty(USED_CAPACITY)
    private final Double usedCapacity;

    public StoragePoolPerformanceInfo(
            final String id,
            final String nodeId,
            final String name,
            final Double responseTime,
            final Double utilization,
            final String storageSystemName,
            final Double totalIOPS,
            final Double usedCapacity) {
        this.id = id;
        this.nodeId = nodeId;
        this.name = name;
        this.responseTime = responseTime;
        this.utilization = utilization;
        this.storageSystemName = storageSystemName;
        this.totalIOPS = totalIOPS;
        this.usedCapacity = usedCapacity;
    }

    public String getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public Double getUtilization() {
        return utilization;
    }

    public String getStorageSystemName() {
        return storageSystemName;
    }

    public Double getTotalIOPS() {
        return totalIOPS;
    }

    public Double getUsedCapacity() {
        return usedCapacity;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("nodeId", nodeId)
                .add("name", name)
                .add("responseTime", responseTime)
                .add("utilization", utilization)
                .add("storageSystemName", storageSystemName)
                .add("totalIOPS", totalIOPS)
                .add("usedCapacity", usedCapacity)
                .toString();
    }


    @SuppressWarnings("PublicInnerClass")
    public static final class StoragePoolPerformanceInfoBuilder {
        private String id;
        private String nodeId;
        private String name;
        private Double responseTime;
        private Double utilization;
        private String storageSystemName;
        private Double totalIOPS;
        private Double usedCapacity;

        private StoragePoolPerformanceInfoBuilder() {
        }

        public StoragePoolPerformanceInfoBuilder withId(final String id) {
            this.id = id;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withNodeId(final String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withName(final String name) {
            this.name = name;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withResponseTime(final Double responseTime) {
            this.responseTime = responseTime;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withUtilization(final Double utilization) {
            this.utilization = utilization;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withStorageSystemName(final String storageSystemName) {
            this.storageSystemName = storageSystemName;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withTotalIOPS(final Double totalIOPS) {
            this.totalIOPS = totalIOPS;
            return this;
        }

        public StoragePoolPerformanceInfoBuilder withUsedCapacity(final Double usedCapacity) {
            this.usedCapacity = usedCapacity;
            return this;
        }

        public StoragePoolPerformanceInfo build() {
            return new StoragePoolPerformanceInfo(id, nodeId, name, responseTime,
                    utilization, storageSystemName, totalIOPS, usedCapacity);
        }

        public static StoragePoolPerformanceInfoBuilder aStoragePoolPerformanceInfo() {
            return new StoragePoolPerformanceInfoBuilder();
        }
    }
}