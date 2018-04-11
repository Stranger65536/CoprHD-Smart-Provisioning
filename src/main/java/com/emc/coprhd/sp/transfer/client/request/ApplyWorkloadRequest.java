/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.client.request;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class ApplyWorkloadRequest {
    private static final String APPLICATIONS_LIST = "applicationsList";

    private final ApplicationsList applicationsList;

    @JsonCreator
    public ApplyWorkloadRequest(@JsonProperty(APPLICATIONS_LIST) final ApplicationsList applicationsList) {
        this.applicationsList = applicationsList;
    }

    public ApplicationsList getApplicationsList() {
        return applicationsList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("applicationsList", applicationsList)
                .toString();
    }
}
