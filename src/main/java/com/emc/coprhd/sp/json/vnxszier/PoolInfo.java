/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.emc.coprhd.sp.supports.vnx.sizer.PoolType;
import com.emc.coprhd.sp.supports.vnx.sizer.TierRaidType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PoolInfo {
    private static final String DRIVE_COUNT = "driveCount";
    private static final String POOL_TYPE = "poolType";
    private static final String DISK_TYPE = "diskType";
    private static final String DRIVE_SIZE = "driveSize";
    private static final String DRIVE_FORM_FACTOR = "driveFormFactor";
    private static final String RAID_TYPE = "raidType";
    private static final String STRIPE_WIDTH = "stripeWidth";
    private static final String HOT_SPARES = "hotSpares";

    @JsonProperty(DRIVE_COUNT)
    private final Integer driveCount;
    @JsonProperty(POOL_TYPE)
    private final PoolType poolType;
    @JsonProperty(DISK_TYPE)
    private final String diskType;
    @JsonProperty(DRIVE_SIZE)
    private final Integer driveSize;
    @JsonProperty(DRIVE_FORM_FACTOR)
    private final String driveFormFactor;
    @JsonProperty(RAID_TYPE)
    private final TierRaidType raidType;
    @JsonProperty(STRIPE_WIDTH)
    private final Integer stripeWidth;
    @JsonProperty(HOT_SPARES)
    private final Integer hotSpares;

    @JsonCreator
    public PoolInfo(
            @JsonProperty(DRIVE_COUNT) final Integer driveCount,
            @JsonProperty(POOL_TYPE) final PoolType poolType,
            @JsonProperty(DISK_TYPE) final String diskType,
            @JsonProperty(DRIVE_SIZE) final Integer driveSize,
            @JsonProperty(DRIVE_FORM_FACTOR) final String driveFormFactor,
            @JsonProperty(RAID_TYPE) final TierRaidType raidType,
            @JsonProperty(STRIPE_WIDTH) final Integer stripeWidth,
            @JsonProperty(HOT_SPARES) final Integer hotSpares) {
        this.driveCount = driveCount;
        this.poolType = poolType;
        this.diskType = diskType;
        this.driveSize = driveSize;
        this.driveFormFactor = driveFormFactor;
        this.raidType = raidType;
        this.stripeWidth = stripeWidth;
        this.hotSpares = hotSpares;
    }

    public Integer getDriveCount() {
        return driveCount;
    }

    public PoolType getPoolType() {
        return poolType;
    }

    public String getDiskType() {
        return diskType;
    }

    public Integer getDriveSize() {
        return driveSize;
    }

    public String getDriveFormFactor() {
        return driveFormFactor;
    }

    public TierRaidType getRaidType() {
        return raidType;
    }

    public Integer getStripeWidth() {
        return stripeWidth;
    }

    public Integer getHotSpares() {
        return hotSpares;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(driveCount)
                .append(poolType)
                .append(diskType)
                .append(driveSize)
                .append(driveFormFactor)
                .append(raidType)
                .append(stripeWidth)
                .append(hotSpares)
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

        final PoolInfo poolInfo = (PoolInfo) o;

        return new EqualsBuilder()
                .append(driveCount, poolInfo.driveCount)
                .append(poolType, poolInfo.poolType)
                .append(diskType, poolInfo.diskType)
                .append(driveSize, poolInfo.driveSize)
                .append(driveFormFactor, poolInfo.driveFormFactor)
                .append(raidType, poolInfo.raidType)
                .append(stripeWidth, poolInfo.stripeWidth)
                .append(hotSpares, poolInfo.hotSpares)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("driveCount", driveCount)
                .add("poolType", poolType)
                .add("diskType", diskType)
                .add("driveSize", driveSize)
                .add("driveFormFactor", driveFormFactor)
                .add("raidType", raidType)
                .add("stripeWidth", stripeWidth)
                .add("hotSpares", hotSpares)
                .toString();
    }
}