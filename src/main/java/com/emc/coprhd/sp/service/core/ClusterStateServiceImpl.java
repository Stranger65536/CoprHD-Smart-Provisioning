/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.AddressInfo;
import com.emc.coprhd.sp.model.ClusterNode;
import com.emc.coprhd.sp.util.ClusterUtils;
import com.emc.coprhd.sp.util.NetworkUtils;
import com.google.common.collect.Sets;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClusterStateServiceImpl implements ClusterStateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterStateServiceImpl.class);

    private final String nodeId;
    private final HazelcastInstance hzInstance;
    private final IMap<String, ClusterNode> nodes;
    private final Set<AddressInfo> ignoredNetworks;
    private final ClusterMembershipListener listener;

    @Autowired
    public ClusterStateServiceImpl(
            final IMap<String, ClusterNode> nodes,
            final HazelcastInstance hzInstance,
            final ClusterMembershipListener listener,
            @Value("${com.emc.coprhd.sp.node-id}") final String nodeId,
            @Value("${com.emc.coprhd.sp.ignored-networks}") final String ignoredNetworks) {
        this.nodes = nodes;
        this.nodeId = nodeId;
        this.listener = listener;
        this.hzInstance = hzInstance;
        this.ignoredNetworks = Stream.of(ignoredNetworks.split(","))
                .map(i -> new AddressInfo("", i))
                .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public String getNodeAddress(final ClusterNode node) {
        try {
            final Set<AddressInfo> localAddresses = NetworkUtils.getHostAddresses();
            localAddresses.removeAll(ignoredNetworks);
            final Set<AddressInfo> nodeAddresses = node.getListenAddresses();
            final Set<AddressInfo> common = Sets.intersection(nodeAddresses, localAddresses);
            LOGGER.debug("Common networks for node {} is {}", node, common);
            return common.stream().map(AddressInfo::getAddress).findFirst().orElse(null);
        } catch (SocketException e) {
            LOGGER.error("Can't get node address {}!", node, e);
            return null;
        }
    }

    @Override
    public List<ClusterNode> getAvailableNodes() {
        return new ArrayList<>(nodes.values());
    }

    @EventListener(ContextRefreshedEvent.class)
    void contextRefreshedEvent() {
        ClusterUtils.addNodeInfoToCluster(ignoredNetworks, nodeId, hzInstance, nodes);
        hzInstance.getCluster().addMembershipListener(listener);
    }
}
