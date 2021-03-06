/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.Set;

public class ClusterNode implements DataSerializable {
    private static final ObjectMapper JSON = new ObjectMapper();

    private String id;
    private long joinTime;
    private Set<AddressInfo> listenAddresses;

    @SuppressWarnings("unused")
    public ClusterNode() {
    }

    public ClusterNode(final String id, final Set<AddressInfo> listenAddresses, final long joinTime) {
        this.id = id;
        this.joinTime = joinTime;
        this.listenAddresses = listenAddresses;
    }

    public String getId() {
        return id;
    }

    public Set<AddressInfo> getListenAddresses() {
        return listenAddresses;
    }

    public long getJoinTime() {
        return joinTime;
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        final ClusterNode other = (ClusterNode) o;

        return new EqualsBuilder()
                .append(id, other.id)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("listenAddresses", listenAddresses)
                .add("joinTime", joinTime)
                .toString();
    }

    @Override
    public void writeData(final ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeLong(joinTime);
        out.writeUTF(JSON.writeValueAsString(listenAddresses));
    }

    @Override
    public void readData(final ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.joinTime = in.readLong();
        this.listenAddresses = JSON.readValue(in.readUTF(), new AddressInfoSetReference());
    }

    private static class AddressInfoSetReference extends TypeReference<Set<AddressInfo>> {
    }
}
