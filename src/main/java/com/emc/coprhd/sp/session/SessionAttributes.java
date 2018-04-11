/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.session;

import com.google.common.base.MoreObjects;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionAttributes {
    private StoragePoolsSessionInfo storagePoolsSessionInfo;

    public StoragePoolsSessionInfo getStoragePoolsSessionInfo() {
        return storagePoolsSessionInfo;
    }

    public SessionAttributes setStoragePoolsSessionInfo(final StoragePoolsSessionInfo storagePoolsSessionInfo) {
        this.storagePoolsSessionInfo = storagePoolsSessionInfo;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("storagePoolsSessionInfo", storagePoolsSessionInfo)
                .toString();
    }
}