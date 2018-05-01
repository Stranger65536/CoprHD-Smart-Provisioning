/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.common;

import com.emc.coprhd.sp.exception.StartupException;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractFallBackable<T extends FallBackable> implements FallBackable {
    protected List<T> clients;
    protected T activeClient;
    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public void setBeanName(final String name) {
        this.name = name;
    }

    protected <R> R performCascadeOperation(final Function<T, R> operation, final Logger log) {
        final Iterator<T> it = clients.subList(clients.indexOf(activeClient), clients.size()).iterator();
        //noinspection BooleanVariableAlwaysNegated
        do {
            try {
                return operation.apply(activeClient);
            } catch (Exception e) {
                log.error("Error during client {} initialization", activeClient.getName(), e);
                if (it.hasNext()) {
                    final T next = it.next();
                    log.info("Switching client to {}", next.getName());
                    activeClient = next;
                } else {
                    throw new StartupException("No more clients available!", e);
                }
            }
        } while (true);
    }
}
