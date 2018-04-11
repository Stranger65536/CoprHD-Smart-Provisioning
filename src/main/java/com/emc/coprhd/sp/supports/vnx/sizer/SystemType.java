/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.supports.vnx.sizer;

import com.emc.coprhd.sp.supports.vnx.sizer.SystemType.SystemTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = SystemTypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum SystemType {
    SYSTEM_TYPE_VNX_31("VNXe 3100"),
    SYSTEM_TYPE_VNX_33("VNXe 3300"),
    SYSTEM_TYPE_VNX_315("VNXe 3150"),
    SYSTEM_TYPE_VNX_51("VNX 5100"),
    SYSTEM_TYPE_VNX_53("VNX 5300"),
    SYSTEM_TYPE_VNX_55("VNX 5500"),
    SYSTEM_TYPE_VNX_55_4("VNX 5500:4"),
    SYSTEM_TYPE_VNX_57("VNX 5700"),
    SYSTEM_TYPE_VNX_75_8("VNX 7500:8"),
    SYSTEM_TYPE_VNX_75_8M("VNX 7500:8M"),
    SYSTEM_TYPE_VNX_75_16("VNX 7500:16"),
    SYSTEM_TYPE_VNX_75_16M("VNX 7500:16M"),
    SYSTEM_TYPE_VNX2_52("VNX5200"),
    SYSTEM_TYPE_VNX2_54("VNX5400"),
    SYSTEM_TYPE_VNX2_56("VNX5600"),
    SYSTEM_TYPE_VNX2_58("VNX5800"),
    SYSTEM_TYPE_VNX2_76("VNX7600"),
    SYSTEM_TYPE_VNX2_80("VNX8000");

    final String value;

    SystemType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static SystemType forValue(final String value) {
        final SystemType[] types = values();
        for (SystemType poolType : types) {
            if (poolType.value.equalsIgnoreCase(value)) {
                return poolType;
            }
        }
        return null;
    }

    static class SystemTypeDeserializer extends JsonDeserializer<SystemType> {
        @Override
        public SystemType deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext) throws IOException {
            return forValue(jsonParser.getText());
        }
    }
}