/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ResponseBody {
    private static final String POOLS = "pools";

    @JsonProperty(POOLS)
    private final List<Pool> pools;

    @JsonCreator
    public ResponseBody(@JsonProperty(POOLS) final List<Pool> pools) {
        this.pools = pools;
    }

    public List<Pool> getPools() {
        return pools;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pools)
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

        final ResponseBody other = (ResponseBody) o;

        return new EqualsBuilder()
                .append(pools, other.pools)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pools", pools)
                .toString();
    }
}
