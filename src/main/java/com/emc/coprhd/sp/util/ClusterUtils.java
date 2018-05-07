/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.util;

import com.emc.coprhd.sp.exception.StartupException;
import com.emc.coprhd.sp.model.AddressInfo;
import com.emc.coprhd.sp.model.ClusterNode;
import com.hazelcast.client.impl.HazelcastClientInstanceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Partition;
import com.hazelcast.spi.properties.HazelcastProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;

public enum ClusterUtils {
    ;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterUtils.class);

    @SuppressWarnings("TypeMayBeWeakened")
    public static void addNodeInfoToCluster(
            final Set<AddressInfo> ignoredNetworks,
            final String nodeId,
            final HazelcastInstance hzInstance,
            final IMap<String, ClusterNode> nodes) {
        try {
            final Set<AddressInfo> addressList = NetworkUtils.getHostAddresses();
            addressList.removeAll(ignoredNetworks);
            final ClusterNode node = new ClusterNode(nodeId, addressList, System.currentTimeMillis());
            LOGGER.info("Node {} has joined the cluster", node);

            final Field f = hzInstance.getConfig().getClass().getDeclaredField("instance");
            f.setAccessible(true);
            final HazelcastProperties properties = ((HazelcastClientInstanceImpl)
                    f.get(hzInstance.getConfig())).getProperties();
            final String localHzAddress = properties.get("hazelcast.client.debug.address");
            final Partition localPartition = hzInstance.getPartitionService().getPartitions().stream()
                    .filter(part -> Objects.equals(localHzAddress, toInetAddress(part)))
                    .findFirst().orElseThrow(() -> new StartupException("No local hz member found!"));
            LOGGER.debug("Local DC hz partition id: {}", localPartition.getPartitionId());
            nodes.put(nodeId + '@' + String.valueOf(localPartition.getPartitionId()), node);
        } catch (IllegalAccessException | SocketException | NoSuchFieldException e) {
            throw new IllegalStateException("Can't add info to cluster!", e);
        }
    }

    private static String toInetAddress(final Partition partition) {
        try {
            return partition.getOwner().getAddress().getInetAddress().getHostAddress();
        } catch (UnknownHostException e) {
            throw new StartupException("No address of partition: " + partition, e);
        }
    }
}
