/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.List;

public class ApplicationsList {
    private static final String APP_FREE_FORM_LIST = "appFreeFormlist";
    private static final String APP_ORACLE_OLTP_LIST = "appOracleOLTPlist";

    @JsonProperty(APP_FREE_FORM_LIST)
    private final List<ApplicationWorkloads> freeFormWorkloads;
    @JsonProperty(APP_ORACLE_OLTP_LIST)
    private final List<OracleOLTPWorkload> oracleOLTPWorkloads;

    @JsonCreator
    public ApplicationsList(
            @JsonProperty(APP_FREE_FORM_LIST) final List<ApplicationWorkloads> freeFormWorkloads,
            @JsonProperty(APP_ORACLE_OLTP_LIST) final List<OracleOLTPWorkload> oracleOLTPWorkloads) {
        this.freeFormWorkloads = freeFormWorkloads;
        this.oracleOLTPWorkloads = oracleOLTPWorkloads;
    }

    public List<ApplicationWorkloads> getFreeFormWorkloads() {
        return freeFormWorkloads;
    }

    public Collection<OracleOLTPWorkload> getOracleOLTPWorkloads() {
        return oracleOLTPWorkloads;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(freeFormWorkloads)
                .append(oracleOLTPWorkloads)
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

        final ApplicationsList other = (ApplicationsList) o;

        return new EqualsBuilder()
                .append(freeFormWorkloads, other.freeFormWorkloads)
                .append(oracleOLTPWorkloads, other.oracleOLTPWorkloads)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("freeFormWorkloads", freeFormWorkloads)
                .add("oracleOLTPWorkloads", oracleOLTPWorkloads)
                .toString();
    }
}
