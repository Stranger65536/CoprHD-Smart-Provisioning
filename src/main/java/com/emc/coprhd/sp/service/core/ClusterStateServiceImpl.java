/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.AddressInfo;
import com.emc.coprhd.sp.model.ClusterNode;
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

@Service
public class ClusterStateServiceImpl implements ClusterStateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterStateServiceImpl.class);

    private final String nodeId;
    private final HazelcastInstance hzInstance;
    private final IMap<String, ClusterNode> nodes;

    @Autowired
    public ClusterStateServiceImpl(
            final IMap<String, ClusterNode> nodes,
            final HazelcastInstance hzInstance,
            @Value("${com.emc.coprhd.sp.node-id}") final String nodeId) {
        this.nodes = nodes;
        this.nodeId = nodeId;
        this.hzInstance = hzInstance;
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public String getNodeAddress(final ClusterNode node) {
        try {
            final Set<AddressInfo> localAddresses = NetworkUtils.getHostAddresses();
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
    void contextRefreshedEvent() throws SocketException {
        final Set<AddressInfo> addressList = NetworkUtils.getHostAddresses();
        final ClusterNode node = new ClusterNode(nodeId, addressList);
        LOGGER.info("Node {} has joined the cluster", node);
        hzInstance.getPartitionService().getPartitions().forEach(part ->
                LOGGER.debug("Part id {} owner: {}", part.getPartitionId(), part.getOwner().getAddress())
        );
//        final Partition localPartition = hzInstance.getPartitionService().getPartitions().stream()
//                .findFirst().orElseThrow(() -> new StartupException("No local hz member found!"));
//        localPartition.getPartitionId();
//        LOGGER.debug("Local DC hz partition id: {}", localPartition.getPartitionId());
        nodes.put(nodeId, node);
    }
}
