package com.emc.coprhd.sp.service.vipr;

import com.emc.coprhd.sp.service.common.AbstractFallBackable;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolParam;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;
import com.emc.storageos.model.vpool.StoragePoolAssignmentChanges;
import com.emc.storageos.model.vpool.StoragePoolAssignments;
import com.emc.storageos.model.vpool.VirtualPoolPoolUpdateParam;
import com.emc.vipr.client.ClientConfig;
import com.emc.vipr.client.ViPRCoreClient;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hazelcast.util.Preconditions.checkNotNull;

@Order(0)
@Service("ViPRClient")
public class ViPRClientImpl extends AbstractFallBackable<ViPRClient> implements ViPRClient {
    @SuppressWarnings("FieldCanBeLocal")
    private final String host;
    private final String login;
    private final String password;
    @SuppressWarnings("FieldCanBeLocal")
    private final int timeout;
    private final ViPRCoreClient viprCoreClient;

    @Autowired
    public ViPRClientImpl(
            @Value("${com.emc.coprhd.sp.vipr.host}") final String host,
            @Value("${com.emc.coprhd.sp.vipr.login}") final String login,
            @Value("${com.emc.coprhd.sp.vipr.password}") final String password,
            @Value("${com.emc.coprhd.sp.vipr.timeout}") final String timeout) {
        this.host = checkNotNull(host, "ViPR host must be specified!");
        this.login = checkNotNull(login, "ViPR login must be specified!");
        this.password = checkNotNull(password, "ViPR password must be specified!");
        //noinspection ConstantConditions
        this.timeout = checkNotNull(Ints.tryParse(timeout), "ViPR timeout must be specified!");
        viprCoreClient = new ViPRCoreClient(new ClientConfig()
                .withHost(this.host)
                .withConnectionTimeout(this.timeout)
                .withIgnoringCertificates(true)
                .withMaxConcurrentTaskRequests(3)
                .withRequestLoggingEnabled()
                .withMaxRetries(3)
                .withMediaType("application/json"));
    }

    @Override
    public void initialize() {
        viprCoreClient.auth().login(login, password);
    }

    @Override
    public Collection<StoragePoolRestRep> getStoragePools() {
        return viprCoreClient.storagePools().getAll();
    }

    @Override
    public StoragePoolRestRep getStoragePool(final URI id) {
        return viprCoreClient.storagePools().get(id);
    }

    @Override
    public StorageSystemRestRep getStorageSystem(final URI id) {
        return viprCoreClient.storageSystems().get(id);
    }

    @Override
    public Collection<BlockVirtualPoolRestRep> getVirtualPools() {
        return viprCoreClient.blockVpools().getAll();
    }

    @Override
    public URI createVirtualPool(final String name, final Collection<URI> pools) {
        final URI poolId = createVirtualPool(name);
        assignStoragePoolsToVirtualPool(pools, poolId);
        return poolId;
    }

    @PreDestroy
    private void destroy() {
        viprCoreClient.auth().forceLogout();
    }

    private URI createVirtualPool(final String name) {
        final BlockVirtualPoolParam poolParam = prepareVirtualPoolParams(name);
        return viprCoreClient.blockVpools().create(poolParam).getId();
    }

    private void assignStoragePoolsToVirtualPool(final Collection<URI> pools, final URI poolId) {
        final VirtualPoolPoolUpdateParam updateParam = prepareAssignParams(pools);
        viprCoreClient.blockVpools().assignStoragePools(poolId, updateParam);
    }

    private BlockVirtualPoolParam prepareVirtualPoolParams(final String name) {
        final BlockVirtualPoolParam poolParam = new BlockVirtualPoolParam();

        poolParam.setName(name);
        poolParam.setDescription("Created using ViPRSizer Pool Manager");
        poolParam.setProvisionType("Thin");
        poolParam.setDriveType("SAS");
        poolParam.setMaxPaths(1);
        poolParam.setProtocols(new HashSet<>(Collections.singletonList("iSCSI")));
        poolParam.setUseMatchedPools(false);
        poolParam.setVarrays(getVArrays());

        return poolParam;
    }

    private Set<String> getVArrays() {
        return viprCoreClient.varrays().getAll().stream().map(i -> i.getId().toString()).collect(Collectors.toSet());
    }

    private static VirtualPoolPoolUpdateParam prepareAssignParams(final Collection<URI> pools) {
        final StoragePoolAssignments assignments = prepareAssignments(pools);
        final StoragePoolAssignmentChanges changes = prepareAssignmentChanges(assignments);
        return prepareUpdateParams(changes);
    }

    private static StoragePoolAssignments prepareAssignments(final Collection<URI> pools) {
        final StoragePoolAssignments assignments = new StoragePoolAssignments();
        assignments.setStoragePools(pools.stream().map(URI::toString).collect(Collectors.toSet()));
        return assignments;
    }

    private static StoragePoolAssignmentChanges prepareAssignmentChanges(final StoragePoolAssignments assignments) {
        final StoragePoolAssignmentChanges changes = new StoragePoolAssignmentChanges();
        changes.setAdd(assignments);
        return changes;
    }

    private static VirtualPoolPoolUpdateParam prepareUpdateParams(final StoragePoolAssignmentChanges changes) {
        final VirtualPoolPoolUpdateParam updateParam = new VirtualPoolPoolUpdateParam();
        updateParam.setStoragePoolAssignmentChanges(changes);
        return updateParam;
    }
}