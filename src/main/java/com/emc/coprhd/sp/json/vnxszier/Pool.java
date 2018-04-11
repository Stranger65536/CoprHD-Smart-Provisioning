/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class Pool {
    private static final String POOL_DISK_UYILIZATION = "poolDiskUyilization";
    private static final String POOL_AVG_RESPONCE_TIME = "poolAvgResponceTime";

    @JsonProperty(POOL_AVG_RESPONCE_TIME)
    private final BigDecimal poolAvgResponceTime;
    @JsonProperty(POOL_DISK_UYILIZATION)
    private final BigDecimal poolDiskUtilization;

    @JsonCreator
    public Pool(
            @JsonProperty(POOL_AVG_RESPONCE_TIME) final BigDecimal poolAvgResponceTime,
            @JsonProperty(POOL_DISK_UYILIZATION) final BigDecimal poolDiskUtilization) {
        this.poolAvgResponceTime = poolAvgResponceTime;
        this.poolDiskUtilization = poolDiskUtilization;
    }

    public BigDecimal getPoolAvgResponceTime() {
        return poolAvgResponceTime;
    }

    public BigDecimal getPoolDiskUtilization() {
        return poolDiskUtilization;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(poolAvgResponceTime)
                .append(poolDiskUtilization)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        final Pool pool = (Pool) o;

        return new EqualsBuilder()
                .append(poolAvgResponceTime, pool.poolAvgResponceTime)
                .append(poolDiskUtilization, pool.poolDiskUtilization)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("poolAvgResponceTime", poolAvgResponceTime)
                .add("poolDiskUtilization", poolDiskUtilization)
                .toString();
    }
}
