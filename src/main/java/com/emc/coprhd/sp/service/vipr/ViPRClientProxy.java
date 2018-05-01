/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.vipr;

import com.emc.coprhd.sp.exception.StartupException;
import com.emc.coprhd.sp.service.common.AbstractFallBackable;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@Primary
@Service("ViPRClientProxy")
public class ViPRClientProxy extends AbstractFallBackable<ViPRClient> implements ViPRClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViPRClientProxy.class);

    @Autowired
    public ViPRClientProxy(final List<ViPRClient> clients) {
        if (clients.isEmpty()) {
            throw new StartupException("At least one ViPR Client has to be defined in context!");
        }
        super.clients = clients;
        super.activeClient = clients.get(0);
    }

    @PostConstruct
    @SuppressWarnings("ReturnOfNull")
    public void init() {
        performCascadeOperation(client -> {
            client.initialize();
            return null;
        }, LOGGER);
    }

    @Override
    public Collection<StoragePoolRestRep> getStoragePools() {
        return performCascadeOperation(ViPRClient::getStoragePools, LOGGER);
    }

    @Override
    public StoragePoolRestRep getStoragePool(final URI id) {
        return performCascadeOperation(client -> client.getStoragePool(id), LOGGER);
    }

    @Override
    public StorageSystemRestRep getStorageSystem(final URI id) {
        return performCascadeOperation(client -> client.getStorageSystem(id), LOGGER);
    }

    @Override
    public Collection<BlockVirtualPoolRestRep> getVirtualPools() {
        return performCascadeOperation(ViPRClient::getVirtualPools, LOGGER);
    }

    @Override
    public URI createVirtualPool(final String name, final Collection<URI> pools) {
        return performCascadeOperation(client -> client.createVirtualPool(name, pools), LOGGER);
    }

    @Override
    public void initialize() {
    }
}
