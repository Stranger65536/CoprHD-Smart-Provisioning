/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.supports.vnx.sizer;

import com.emc.coprhd.sp.supports.vnx.sizer.PoolType.PoolTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = PoolTypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum PoolType {
    POOL_TYPE_RG_META("POOL_TYPE_RG_META"),
    POOL_TYPE_POOL("POOL_TYPE_POOL");

    final String value;

    PoolType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    private static PoolType forValue(final String value) {
        final PoolType[] types = values();
        for (PoolType poolType : types) {
            if (poolType.value.equalsIgnoreCase(value)) {
                return poolType;
            }
        }
        return null;
    }

    static class PoolTypeDeserializer extends JsonDeserializer<PoolType> {
        @Override
        public PoolType deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext) throws IOException {
            return forValue(jsonParser.getText());
        }
    }
}