/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HzConfig {
    private final String xmlLocation;

    @Autowired
    public HzConfig(@Value("${hazelcast.client.config}") final String xmlLocation) {
        this.xmlLocation = xmlLocation;
    }

    @Bean
    public ClientConfig hzClientConfig() throws IOException {
        return new XmlClientConfigBuilder(xmlLocation).build();
    }

    @Bean
    @Autowired
    public HazelcastInstance hzClient(final ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
