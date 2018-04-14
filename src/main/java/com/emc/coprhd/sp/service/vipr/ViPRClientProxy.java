package com.emc.coprhd.sp.service.vipr;

import com.emc.coprhd.sp.exception.StartupException;
import com.emc.storageos.model.pools.StoragePoolRestRep;
import com.emc.storageos.model.systems.StorageSystemRestRep;
import com.emc.storageos.model.vpool.BlockVirtualPoolRestRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@Primary
@Service("ViPRClientProxy")
public class ViPRClientProxy implements ViPRClient, BeanNameAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViPRClientProxy.class);

    private final List<ViPRClient> clients;
    private volatile ViPRClient activeClient;
    private String name;

    @Autowired
    public ViPRClientProxy(final List<ViPRClient> clients) {
        if (clients.isEmpty()) {
            throw new StartupException("At least one ViPR Client has to be defined in context!");
        }
        this.clients = clients;
        this.activeClient = clients.get(0);
    }

    @PostConstruct
    @SuppressWarnings("ReturnOfNull")
    public void init() {
        performCascadeOperation(client -> {
            client.login();
            return null;
        });
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public void login() {
        performCascadeOperation(client -> {
            client.login();
            return null;
        });
    }

    @Override
    public Collection<StoragePoolRestRep> getStoragePools() {
        return performCascadeOperation(ViPRClient::getStoragePools);
    }

    @Override
    public StoragePoolRestRep getStoragePool(final URI id) {
        return performCascadeOperation(client -> client.getStoragePool(id));
    }

    @Override
    public StorageSystemRestRep getStorageSystem(final URI id) {
        return performCascadeOperation(client -> client.getStorageSystem(id));
    }

    @Override
    public Collection<BlockVirtualPoolRestRep> getVirtualPools() {
        return performCascadeOperation(ViPRClient::getVirtualPools);
    }

    @Override
    public URI createVirtualPool(final String name, final Collection<URI> pools) {
        return performCascadeOperation(client -> client.createVirtualPool(name, pools));
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

    private <T> T performCascadeOperation(final Function<ViPRClient, T> operation) {
        final Iterator<ViPRClient> it = clients.subList(clients.indexOf(activeClient), clients.size()).iterator();
        //noinspection BooleanVariableAlwaysNegated
        do {
            try {
                return operation.apply(activeClient);
            } catch (Exception e) {
                LOGGER.error("Error during client {} initialization", activeClient.getName(), e);
                if (it.hasNext()) {
                    final ViPRClient next = it.next();
                    LOGGER.info("Switching ViPR client to {}", next.getName());
                    activeClient = next;
                } else {
                    throw new StartupException("No more ViPR clients available!", e);
                }
            }
        } while (true);
    }
}
