/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OracleOLTPUser {
    private static final String USER_COUNT = "userCount";
    private static final String USER_NAME = "userName";
    private static final String USER_TYPE = "userType";

    @JsonProperty(USER_COUNT)
    private final Integer userCount;
    @JsonProperty(USER_NAME)
    private final String userName;
    @JsonProperty(USER_TYPE)
    private final Integer userType;

    @JsonCreator
    public OracleOLTPUser(
            @JsonProperty(USER_COUNT) final Integer userCount,
            @JsonProperty(USER_NAME) final String userName,
            @JsonProperty(USER_TYPE) final Integer userType) {
        this.userCount = userCount;
        this.userName = userName;
        this.userType = userType;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getUserType() {
        return userType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userCount)
                .append(userName)
                .append(userType)
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

        final OracleOLTPUser other = (OracleOLTPUser) o;

        return new EqualsBuilder()
                .append(userCount, other.userCount)
                .append(userName, other.userName)
                .append(userType, other.userType)
                .isEquals();
    }
}
