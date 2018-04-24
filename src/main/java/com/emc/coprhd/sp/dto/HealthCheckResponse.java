/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.dto;

public class HealthCheckResponse {
    private final String status;
    private final Object data;

    public HealthCheckResponse(final String status) {
        this.status = status;
        this.data = null;
    }

    public HealthCheckResponse(final String status, final Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
