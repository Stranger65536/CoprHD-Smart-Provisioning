/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.vnx.sizer.request;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.emc.coprhd.sp.json.vnxszier.PoolInfo;
import com.emc.coprhd.sp.json.vnxszier.SystemPreferences;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class VNXSizerRequest {
    private static final String APPLICATIONS_LIST = "applicationsList";
    private static final String SIZER_PROJECT_TYPE = "sizerProjectType";
    private static final String POOL_INFO = "poolInfo";
    private static final String SYSTEM_PREFERENCES = "systemPreferences";

    @JsonProperty(APPLICATIONS_LIST)
    private final ApplicationsList applicationsList;
    @JsonProperty(SIZER_PROJECT_TYPE)
    private final Integer sizerProjectType;
    @JsonProperty(POOL_INFO)
    private final PoolInfo poolInfo;
    @JsonProperty(SYSTEM_PREFERENCES)
    private final SystemPreferences systemPreferences;

    public VNXSizerRequest(
            final ApplicationsList applicationsList,
            final Integer sizerProjectType,
            final PoolInfo poolInfo,
            final SystemPreferences systemPreferences) {
        this.applicationsList = applicationsList;
        this.sizerProjectType = sizerProjectType;
        this.poolInfo = poolInfo;
        this.systemPreferences = systemPreferences;
    }

    public ApplicationsList getApplicationsList() {
        return applicationsList;
    }

    public Integer getSizerProjectType() {
        return sizerProjectType;
    }

    public PoolInfo getPoolInfo() {
        return poolInfo;
    }

    public SystemPreferences getSystemPreferences() {
        return systemPreferences;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("applicationsList", applicationsList)
                .add("sizerProjectType", sizerProjectType)
                .add("poolInfo", poolInfo)
                .add("systemPreferences", systemPreferences)
                .toString();
    }
}
