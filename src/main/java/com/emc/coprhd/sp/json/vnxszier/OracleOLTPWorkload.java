/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class OracleOLTPWorkload {
    private static final String DESCRIPTION = "description";
    private static final String OLTP_ACTIVE_TABLE_GB = "oltpActiveTableGB";
    private static final String TYPE = "type";
    private static final String APP_NAME = "appName";
    private static final String OLTP_TRANSACTION_DURATION = "oltpTransactionDuration";
    private static final String ID = "id";
    private static final String B_THIN_LUN = "bThinLUN";
    private static final String USERS_LIST = "usersList";

    @JsonProperty(USERS_LIST)
    private final List<OracleOLTPUser> oracleOLTPUserList;
    @JsonProperty(B_THIN_LUN)
    private final Boolean thinLun;
    @JsonProperty(DESCRIPTION)
    private final String description;
    @JsonProperty(OLTP_ACTIVE_TABLE_GB)
    private final Integer activeTableGB;
    @JsonProperty(TYPE)
    private final Integer type;
    @JsonProperty(APP_NAME)
    private final String appName;
    @JsonProperty(OLTP_TRANSACTION_DURATION)
    private final Integer transactionDuration;
    @JsonProperty(ID)
    private final Integer id;

    public OracleOLTPWorkload(
            @JsonProperty(USERS_LIST) final List<OracleOLTPUser> oracleOLTPUserList,
            @JsonProperty(B_THIN_LUN) final Boolean thinLun,
            @JsonProperty(DESCRIPTION) final String description,
            @JsonProperty(OLTP_ACTIVE_TABLE_GB) final Integer activeTableGB,
            @JsonProperty(TYPE) final Integer type,
            @JsonProperty(APP_NAME) final String appName,
            @JsonProperty(OLTP_TRANSACTION_DURATION) final Integer transactionDuration,
            @JsonProperty(ID) final Integer id) {
        this.oracleOLTPUserList = oracleOLTPUserList;
        this.thinLun = thinLun;
        this.description = description;
        this.activeTableGB = activeTableGB;
        this.type = type;
        this.appName = appName;
        this.transactionDuration = transactionDuration;
        this.id = id;
    }

    public List<OracleOLTPUser> getOracleOLTPUserList() {
        return oracleOLTPUserList;
    }

    public Boolean getThinLun() {
        return thinLun;
    }

    public String getDescription() {
        return description;
    }

    public Integer getActiveTableGB() {
        return activeTableGB;
    }

    public Integer getType() {
        return type;
    }

    public String getAppName() {
        return appName;
    }

    public Integer getTransactionDuration() {
        return transactionDuration;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(oracleOLTPUserList)
                .append(thinLun)
                .append(description)
                .append(activeTableGB)
                .append(type)
                .append(appName)
                .append(transactionDuration)
                .append(id)
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

        final OracleOLTPWorkload other = (OracleOLTPWorkload) o;

        return new EqualsBuilder()
                .append(oracleOLTPUserList, other.oracleOLTPUserList)
                .append(thinLun, other.thinLun)
                .append(description, other.description)
                .append(activeTableGB, other.activeTableGB)
                .append(type, other.type)
                .append(appName, other.appName)
                .append(transactionDuration, other.transactionDuration)
                .append(id, other.id)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("oracleOLTPUserList", oracleOLTPUserList)
                .add("thinLun", thinLun)
                .add("description", description)
                .add("activeTableGB", activeTableGB)
                .add("type", type)
                .add("appName", appName)
                .add("transactionDuration", transactionDuration)
                .add("id", id)
                .toString();
    }
}
