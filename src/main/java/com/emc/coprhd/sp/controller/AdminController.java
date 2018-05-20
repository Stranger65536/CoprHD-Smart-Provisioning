/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Collections.singletonMap;

@RestController
public class AdminController {
    private final String nodeId;

    @Autowired
    public AdminController(@Value("${com.emc.coprhd.sp.node-id}") final String nodeId) {
        this.nodeId = nodeId;
    }

    @ResponseBody
    @GetMapping(value = "/nodeId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getNodeId() {
        return singletonMap("nodeId", nodeId);
    }
}
