package com.emc.coprhd.sp.service.vipr;

import com.emc.storageos.model.DataObjectRestRep;
import com.emc.storageos.model.errorhandling.ServiceErrorRestRep;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;
import com.emc.vipr.client.exceptions.ServiceErrorException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Order(1)
@Service("ViPRClientStub")
public class ViPRClientStubImpl implements ViPRClient, BeanNameAware {
    private final File storageSystemJsonPath;
    private final File storagePoolsJsonPath;
    private final File virtualPoolsJsonPath;
    private final ObjectMapper objectMapper;

    private Collection<StoragePoolRestRep> storagePools;
    private Collection<BlockVirtualPoolRestRep> virtualPools;
    private Map<URI, StoragePoolRestRep> storagePoolsMap;
    private StorageSystemRestRep storageSystem;

    private String name;

    @Autowired
    public ViPRClientStubImpl(
            @Value("${com.emc.coprhd.sp.vipr.stub.storage-system}") final String storageSystemJsonPath,
            @Value("${com.emc.coprhd.sp.vipr.stub.storage-pools}") final String storagePoolsJsonPath,
            @Value("${com.emc.coprhd.sp.vipr.stub.virtual-pools}") final String virtualPoolsJsonPath,
            final ObjectMapper mapper) {
        this.storageSystemJsonPath = new File(checkNotNull(storageSystemJsonPath,
                "Storage System json path must be specified!"));
        this.storagePoolsJsonPath = new File(checkNotNull(storagePoolsJsonPath,
                "Storage Pools json path must be specified!"));
        this.virtualPoolsJsonPath = new File(checkNotNull(virtualPoolsJsonPath,
                "Virtual Pools json path must be specified!"));
        this.objectMapper = mapper;
    }

    @Override
    public void login() {
        checkState(this.storageSystemJsonPath.exists(),
                "Storage System json file must exist at "
                        + this.storageSystemJsonPath + '!');
        checkState(this.storagePoolsJsonPath.exists(),
                "Storage Pools json file must exist at "
                        + this.storagePoolsJsonPath + '!');
        checkState(this.virtualPoolsJsonPath.exists(),
                "Virtual Pools json file must exist at "
                        + this.virtualPoolsJsonPath + '!');

        try {
            storageSystem = prepareStorageSystem();
            storagePools = prepareStoragePools();
            virtualPools = prepareVirtualPools();
            storagePoolsMap = prepareStoragePoolsMap(storagePools);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read stub config!", e);
        }
    }

    @Override
    public Collection<StoragePoolRestRep> getStoragePools() {
        return storagePools;
    }

    @Override
    public StoragePoolRestRep getStoragePool(final URI id) {
        return storagePoolsMap.get(id);
    }

    @Override
    public StorageSystemRestRep getStorageSystem(final URI id) {
        return storageSystem;
    }

    @Override
    public Collection<BlockVirtualPoolRestRep> getVirtualPools() {
        return virtualPools;
    }

    @Override
    public URI createVirtualPool(final String name, final Collection<URI> pools) {
        if (virtualPools.stream().anyMatch(i -> i.getName().equals(name))) {
            final ServiceErrorRestRep error = new ServiceErrorRestRep();
            error.setCode(HttpStatus.CONFLICT.value());
            error.setDetailedMessage("A component/resource with the label " + name + " already exists.");
            throw new ServiceErrorException(error);
        } else {
            return virtualPools.iterator().next().getId();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public void setBeanName(final String name) {
        this.name = name;
    }

    private StorageSystemRestRep prepareStorageSystem() throws IOException {
        return objectMapper.readValue(storageSystemJsonPath, StorageSystemRestRep.class);
    }

    private Collection<StoragePoolRestRep> prepareStoragePools() throws IOException {
        return objectMapper.readValue(storagePoolsJsonPath, new StoragePoolsTypeReference());
    }

    private Collection<BlockVirtualPoolRestRep> prepareVirtualPools() throws IOException {
        return objectMapper.readValue(virtualPoolsJsonPath, new VirtualPoolsTypeReference());
    }

    private static Map<URI, StoragePoolRestRep> prepareStoragePoolsMap(
            final Collection<StoragePoolRestRep> storagePools) {
        return storagePools.stream().collect(Collectors.toMap(DataObjectRestRep::getId, i -> i));
    }

    private static class StoragePoolsTypeReference extends TypeReference<Collection<StoragePoolRestRep>> {
    }

    private static class VirtualPoolsTypeReference extends TypeReference<Collection<BlockVirtualPoolRestRep>> {
    }
}
