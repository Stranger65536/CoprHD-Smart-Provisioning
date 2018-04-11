/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.emc.coprhd.sp.supports.vnx.sizer.DaeStrategy;
import com.emc.coprhd.sp.supports.vnx.sizer.SystemFlareVersion;
import com.emc.coprhd.sp.supports.vnx.sizer.SystemType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SystemPreferences {
    private static final String SYSTEM_TYPE = "systemType";
    private static final String SYSTEM_FLARE_VERSION = "systemFlareVersion";
    private static final String DAE_STRATEGY = "DAEStrategy";
    private static final String UNIFIED = "unified";
    private static final String COMPACT_SYSTEM_REQUESTED = "compactSystemRequested";
    private static final String DO_BEST_FIT = "doBestFit";
    private static final String DATA_MOVERS = "dataMovers";
    private static final String SYSTEM_UTILIZATION = "systemUtilization";
    private static final String SYSTEM_FLARE_VERSION_VNXE = "systemFlareVersionVNXE";
    private static final String DDECAY_SYSTEM_RANGE = "ddecay_systemRange";
    private static final String DDECAY_MONOLITIC_STRIPE_WIDTH = "ddecay_monoliticStripeWidth";

    @JsonProperty(SYSTEM_TYPE)
    private final SystemType systemType;
    @JsonProperty(SYSTEM_FLARE_VERSION)
    private final SystemFlareVersion systemFlareVersion;
    @JsonProperty(DAE_STRATEGY)
    private final DaeStrategy daeStrategy;
    @JsonProperty(UNIFIED)
    private final Boolean unified;
    @JsonProperty(COMPACT_SYSTEM_REQUESTED)
    private final Boolean compactSystemRequested;
    @JsonProperty(DO_BEST_FIT)
    private final Boolean doBestFit;
    @JsonProperty(DATA_MOVERS)
    private final Integer dataMovers;
    @JsonProperty(SYSTEM_UTILIZATION)
    private final Integer systemUtilization;
    @JsonProperty(SYSTEM_FLARE_VERSION_VNXE)
    private final String systemFlareVersionVnxE;
    @JsonProperty(DDECAY_SYSTEM_RANGE)
    private final String dataDecaySystemRange;
    @JsonProperty(DDECAY_MONOLITIC_STRIPE_WIDTH)
    private final Integer dataDecayMonoliticStripeWidth;

    @JsonCreator
    public SystemPreferences(
            @JsonProperty(SYSTEM_TYPE) final SystemType systemType,
            @JsonProperty(SYSTEM_FLARE_VERSION) final SystemFlareVersion systemFlareVersion,
            @JsonProperty(DAE_STRATEGY) final DaeStrategy daeStrategy,
            @JsonProperty(UNIFIED) final Boolean unified,
            @JsonProperty(COMPACT_SYSTEM_REQUESTED) final Boolean compactSystemRequested,
            @JsonProperty(DO_BEST_FIT) final Boolean doBestFit,
            @JsonProperty(DATA_MOVERS) final Integer dataMovers,
            @JsonProperty(SYSTEM_UTILIZATION) final Integer systemUtilization,
            @JsonProperty(SYSTEM_FLARE_VERSION_VNXE) final String systemFlareVersionVnxE,
            @JsonProperty(DDECAY_SYSTEM_RANGE) final String dataDecaySystemRange,
            @JsonProperty(DDECAY_MONOLITIC_STRIPE_WIDTH) final Integer dataDecayMonoliticStripeWidth) {
        this.systemType = systemType;
        this.systemFlareVersion = systemFlareVersion;
        this.daeStrategy = daeStrategy;
        this.unified = unified;
        this.compactSystemRequested = compactSystemRequested;
        this.doBestFit = doBestFit;
        this.dataMovers = dataMovers;
        this.systemUtilization = systemUtilization;
        this.systemFlareVersionVnxE = systemFlareVersionVnxE;
        this.dataDecaySystemRange = dataDecaySystemRange;
        this.dataDecayMonoliticStripeWidth = dataDecayMonoliticStripeWidth;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public SystemFlareVersion getSystemFlareVersion() {
        return systemFlareVersion;
    }

    public DaeStrategy getDaeStrategy() {
        return daeStrategy;
    }

    public Boolean getUnified() {
        return unified;
    }

    public Boolean getCompactSystemRequested() {
        return compactSystemRequested;
    }

    public Boolean getDoBestFit() {
        return doBestFit;
    }

    public Integer getDataMovers() {
        return dataMovers;
    }

    public Integer getSystemUtilization() {
        return systemUtilization;
    }

    public String getSystemFlareVersionVnxE() {
        return systemFlareVersionVnxE;
    }

    public String getDataDecaySystemRange() {
        return dataDecaySystemRange;
    }

    public Integer getDataDecayMonoliticStripeWidth() {
        return dataDecayMonoliticStripeWidth;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(systemType)
                .append(systemFlareVersion)
                .append(daeStrategy)
                .append(unified)
                .append(compactSystemRequested)
                .append(doBestFit)
                .append(dataMovers)
                .append(systemUtilization)
                .append(systemFlareVersionVnxE)
                .append(dataDecaySystemRange)
                .append(dataDecayMonoliticStripeWidth)
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

        final SystemPreferences other = (SystemPreferences) o;

        return new EqualsBuilder()
                .append(systemType, other.systemType)
                .append(systemFlareVersion, other.systemFlareVersion)
                .append(daeStrategy, other.daeStrategy)
                .append(unified, other.unified)
                .append(compactSystemRequested, other.compactSystemRequested)
                .append(doBestFit, other.doBestFit)
                .append(dataMovers, other.dataMovers)
                .append(systemUtilization, other.systemUtilization)
                .append(systemFlareVersionVnxE, other.systemFlareVersionVnxE)
                .append(dataDecaySystemRange, other.dataDecaySystemRange)
                .append(dataDecayMonoliticStripeWidth, other.dataDecayMonoliticStripeWidth)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("systemType", systemType)
                .add("systemFlareVersion", systemFlareVersion)
                .add("daeStrategy", daeStrategy)
                .add("unified", unified)
                .add("compactSystemRequested", compactSystemRequested)
                .add("doBestFit", doBestFit)
                .add("dataMovers", dataMovers)
                .add("systemUtilization", systemUtilization)
                .add("systemFlareVersionVnxE", systemFlareVersionVnxE)
                .add("dataDecaySystemRange", dataDecaySystemRange)
                .add("dataDecayMonoliticStripeWidth", dataDecayMonoliticStripeWidth)
                .toString();
    }
}
