/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.srm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class SRMPoolInfo {
    private static final String NAME = "name";
    private static final String UTILIZATION = "utilization";
    private static final String RESPONSE_TIME = "responseTime";
    private static final String TOTAL_IOPS = "totalIOPS";
    private static final String USED_CAPACITY = "usedCapacity";
    private static final String DISKS = "disks";

    private final String name;
    private final Double utilization;
    private final Double responseTime;
    private final Double totalIOPS;
    private final Double usedCapacity;
    private final List<DiskInfo> disks;

    @JsonCreator
    public SRMPoolInfo(
            @JsonProperty(NAME) final String name,
            @JsonProperty(UTILIZATION) final Double utilization,
            @JsonProperty(RESPONSE_TIME) final Double responseTime,
            @JsonProperty(TOTAL_IOPS) final Double totalIOPS,
            @JsonProperty(USED_CAPACITY) final Double usedCapacity,
            @JsonProperty(DISKS) final List<DiskInfo> disks) {
        this.name = name;
        this.utilization = utilization;
        this.responseTime = responseTime;
        this.totalIOPS = totalIOPS;
        this.usedCapacity = usedCapacity;
        this.disks = disks;
    }

    public String getName() {
        return name;
    }

    public Double getUtilization() {
        return utilization;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public Double getTotalIOPS() {
        return totalIOPS;
    }

    public Double getUsedCapacity() {
        return usedCapacity;
    }

    public List<DiskInfo> getDisks() {
        return disks;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("utilization", utilization)
                .add("responseTime", responseTime)
                .add("totalIOPS", totalIOPS)
                .add("usedCapacity", usedCapacity)
                .add("disks", disks)
                .toString();
    }
}
