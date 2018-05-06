/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.net.util.SubnetUtils;

public class AddressInfo {
    private final String address;
    private final String netAddress;

    @SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision"})
    public AddressInfo(final short maskPrefix, final byte[] address) {
        this.address = numericToTextFormat(address);
        this.netAddress = new SubnetUtils(this.address + '/' + maskPrefix).getInfo().getNetworkAddress();
    }

    @JsonCreator
    public AddressInfo(
            @JsonProperty("address") final String address,
            @JsonProperty("netAddress") final String netAddress) {
        this.address = address;
        this.netAddress = netAddress;
    }

    @JsonGetter("address")
    public String getAddress() {
        return address;
    }

    @JsonGetter("netAddress")
    public String getNetAddress() {
        return netAddress;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(netAddress)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        final AddressInfo other = (AddressInfo) o;

        return new EqualsBuilder()
                .append(netAddress, other.netAddress)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("netAddress", netAddress)
                .toString();
    }

    @SuppressWarnings("MagicNumber")
    private static String numericToTextFormat(final byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + '.' + (src[2] & 0xff) + '.' + (src[3] & 0xff);
    }
}
