/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.srm;

import com.emc.coprhd.sp.exception.StartupException;
import com.emc.coprhd.sp.service.common.AbstractFallBackable;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Primary
@Service("SRMClientProxy")
public class SRMClientProxy extends AbstractFallBackable<SRMClient> implements SRMClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SRMClientProxy.class);

    @Autowired
    public SRMClientProxy(final List<SRMClient> clients) {
        if (clients.isEmpty()) {
            throw new StartupException("At least one SRM Client has to be defined in context!");
        }
        this.clients = clients;
        this.activeClient = clients.get(0);
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
    public SRMPoolInfo getStoragePoolInfoByName(final String poolName) {
        return performCascadeOperation(client -> client.getStoragePoolInfoByName(poolName), LOGGER);
    }

    @Override
    public void initialize() {

    }
}
