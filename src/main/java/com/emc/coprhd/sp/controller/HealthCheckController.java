/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.dao.mongo.VirtualPool;
import com.emc.coprhd.sp.dao.mongo.VirtualPool.VirtualPoolBuilder;
import com.emc.coprhd.sp.dto.HealthCheckResponse;
import com.emc.coprhd.sp.repository.mongo.MongoDao;
import com.emc.coprhd.sp.util.RuntimeUtils;
import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

    private final MongoDao mongoDao;
    private final HazelcastInstance hzInstance;

    @Autowired
    public HealthCheckController(
            final MongoDao mongoDao,
            final HazelcastInstance hzInstance) {
        this.mongoDao = mongoDao;
        this.hzInstance = hzInstance;
    }

    @ResponseBody
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheckResponse healthCheck() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final HealthCheckResponse response = new HealthCheckResponse("ok");
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/mongo", produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheckResponse testMongo() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        final VirtualPool pool = VirtualPoolBuilder.aVirtualPool().withName("yep").build();
        mongoDao.insert(pool);
        final String id = pool.getId();
        final VirtualPool foundPool = mongoDao.findById(id);
        Preconditions.checkNotNull(foundPool);
        Preconditions.checkArgument("yep".equals(foundPool.getName()));
        mongoDao.delete(pool);
        final HealthCheckResponse response = new HealthCheckResponse("ok");
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/test/hz", produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheckResponse testHz() {
        LOGGER.debug(RuntimeUtils.enterMethodMessage());
        hzInstance.getMap("nodes").size();
        final HealthCheckResponse response = new HealthCheckResponse("ok");
        LOGGER.debug(RuntimeUtils.exitMethodMessage());
        return response;
    }
}
