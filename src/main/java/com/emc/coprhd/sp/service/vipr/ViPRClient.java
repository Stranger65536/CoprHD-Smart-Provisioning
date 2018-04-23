/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.vipr;

import com.emc.coprhd.sp.service.common.FallBackable;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;

import java.net.URI;
import java.util.Collection;

public interface ViPRClient extends FallBackable {
    Collection<StoragePoolRestRep> getStoragePools();

    StoragePoolRestRep getStoragePool(final URI id);

    StorageSystemRestRep getStorageSystem(final URI id);

    Collection<BlockVirtualPoolRestRep> getVirtualPools();

    URI createVirtualPool(final String name, final Collection<URI> pools);
}
