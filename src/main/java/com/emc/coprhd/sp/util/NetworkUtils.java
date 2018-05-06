/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.util;

import com.emc.coprhd.sp.model.AddressInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum NetworkUtils {
    ;
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

    public static Set<AddressInfo> getHostAddresses() throws SocketException {
        final Set<AddressInfo> result = new HashSet<>(10);
        final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets)) {
            final String name = netint.getDisplayName();
            final List<InterfaceAddress> addresses = netint.getInterfaceAddresses();
            if (addresses.stream().noneMatch(address -> address.getAddress().isLoopbackAddress())) {
                final List<AddressInfo> converted = addresses.stream()
                        .filter(address -> address.getAddress() instanceof Inet4Address)
                        .map(addr -> new AddressInfo(addr.getNetworkPrefixLength(), addr.getAddress().getAddress()))
                        .collect(Collectors.toList());
                result.addAll(converted);
                LOGGER.debug("Interface: {}, addresses: {}", name, converted);
            }
        }

        return result;
    }
}
