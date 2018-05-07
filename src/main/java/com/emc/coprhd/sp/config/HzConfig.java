/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.config;

import com.emc.coprhd.sp.model.ClusterNode;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
    @Conditional(NonLocalRunConditional.class)
    public ClientConfig hzClientConfig() throws IOException {
        return new XmlClientConfigBuilder(xmlLocation).build();
    }

    @Bean
    @Autowired
    @Conditional(NonLocalRunConditional.class)
    public HazelcastInstance hzClient(final ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Bean
    @Conditional(LocalRunConditional.class)
    public HazelcastInstance localHzClient() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    @Autowired
    public IMap<String, ClusterNode> nodesMap(final HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("nodes");
    }
}
