/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
/*
 * YEP
 */
package com.emc.coprhd.sp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = HazelcastAutoConfiguration.class)
@ComponentScan("com.emc.coprhd.sp")
@EnableMongoRepositories(basePackages = {"com.emc.coprhd.sp.dao.mongo", "com.emc.coprhd.sp.repository.mongo"})
@SuppressWarnings({"UtilityClassCanBeEnum", "NonFinalUtilityClass"})
public class CoprHDSM {
    public static void main(final String[] args) {
        SpringApplication.run(CoprHDSM.class);
    }
}
