/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.AddressInfo;
import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.util.ClusterUtils;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClusterMembershipListener implements MembershipListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterMembershipListener.class);

    private final String nodeId;
    private final HazelcastInstance hzInstance;
    private final IMap<String, ClusterNode> nodes;
    private final Set<AddressInfo> ignoredNetworks;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ClusterMembershipListener(
            final IMap<String, ClusterNode> nodes,
            final HazelcastInstance hzInstance,
            @Value("${com.emc.coprhd.sp.node-id}") final String nodeId,
            @Value("${com.emc.coprhd.sp.ignored-networks}") final String ignoredNetworks) {
        this.nodes = nodes;
        this.nodeId = nodeId;
        this.hzInstance = hzInstance;
        this.ignoredNetworks = Stream.of(ignoredNetworks.split(","))
                .map(i -> new AddressInfo("", i))
                .collect(Collectors.toSet());
    }

    @Override
    public void memberAdded(final MembershipEvent membershipEvent) {
        ClusterUtils.addNodeInfoToCluster(ignoredNetworks, nodeId, hzInstance, nodes);
    }

    @Override
    public void memberRemoved(final MembershipEvent event) {
        try {
            LOGGER.info("Node left : {}", event.getMember().getAddress().getInetAddress().getHostAddress());
        } catch (UnknownHostException ignored) {
            LOGGER.info("Node left : {}", event.getMember().getUuid());
        }
    }

    @Override
    public void memberAttributeChanged(final MemberAttributeEvent event) {
        try {
            LOGGER.info("Node changed : {}", event.getMember().getAddress().getInetAddress().getHostAddress());
        } catch (UnknownHostException ignored) {
            LOGGER.info("Node changed : {}", event.getMember().getUuid());
        }
    }
}
