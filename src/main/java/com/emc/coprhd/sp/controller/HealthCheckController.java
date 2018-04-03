/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.dto.HealthCheckResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @ResponseBody
    @RequestMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse("ok");
    }
}
