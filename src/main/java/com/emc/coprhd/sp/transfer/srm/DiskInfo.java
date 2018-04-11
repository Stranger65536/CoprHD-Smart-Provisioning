/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.srm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class DiskInfo {
    private static final String NAME = "name";
    private static final String RESPONSE_TIME = "responseTime";
    private static final String UTILIZATION = "utilization";
    private static final String READ_IOPS = "readIOPS";
    private static final String WRITE_IOPS = "writeIOPS";

    private final String name;
    private final Double responseTime;
    private final Double utilization;
    private final Double readIOPS;
    private final Double writeIOPS;

    @JsonCreator
    public DiskInfo(
            @JsonProperty(NAME) final String name,
            @JsonProperty(RESPONSE_TIME) final Double responseTime,
            @JsonProperty(UTILIZATION) final Double utilization,
            @JsonProperty(READ_IOPS) final Double readIOPS,
            @JsonProperty(WRITE_IOPS) final Double writeIOPS) {
        this.name = name;
        this.responseTime = responseTime;
        this.utilization = utilization;
        this.readIOPS = readIOPS;
        this.writeIOPS = writeIOPS;
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

    public Double getReadIOPS() {
        return readIOPS;
    }

    public Double getWriteIOPS() {
        return writeIOPS;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("responseTime", responseTime)
                .add("utilization", utilization)
                .add("readIOPS", readIOPS)
                .add("writeIOPS", writeIOPS)
                .toString();
    }
}
