/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class ResponseError {
    private static final String CODE = "code";
    private static final String DESCRIPTION = "description";

    @JsonProperty(CODE)
    private final Long code;
    @JsonProperty(DESCRIPTION)
    private final String description;

    @JsonCreator
    public ResponseError(
            @JsonProperty(CODE) final Long code,
            @JsonProperty(DESCRIPTION) final String description) {
        this.description = description;
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(description)
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

        final ResponseError other = (ResponseError) o;

        return new EqualsBuilder()
                .append(code, other.code)
                .append(description, other.description)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("description", description)
                .toString();
    }
}
