/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.dto;

public class HealthCheckResponse {
    private final String status;

    public HealthCheckResponse(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
