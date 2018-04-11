/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.supports.vnx.sizer;

import com.emc.coprhd.sp.supports.vnx.sizer.SystemFlareVersion.FlareVersionTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = FlareVersionTypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum SystemFlareVersion {
    FLARE_VERSION_31("OE 31"),
    FLARE_VERSION_31_MR1("OE 31 MR1"),
    FLARE_VERSION_32("OE 32"),
    FLARE_VERSION_33("OE 33"),
    FLARE_VERSION_33_MR1("OE 33 SP2"),
    FLARE_VERSION_VNXE_MR2("OE VNXE_MR2"),
    FLARE_VERSION_VNXE_MR3("OE VNXE_MR3"),
    FLARE_VERSION_VNXE_MR4("OE VNXE_MR4");

    final String value;

    SystemFlareVersion(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static SystemFlareVersion forValue(final String value) {
        final SystemFlareVersion[] types = values();
        for (SystemFlareVersion poolType : types) {
            if (poolType.value.equalsIgnoreCase(value)) {
                return poolType;
            }
        }
        return null;
    }

    static class FlareVersionTypeDeserializer extends JsonDeserializer<SystemFlareVersion> {
        @Override
        public SystemFlareVersion deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext) throws IOException {
            return forValue(jsonParser.getText());
        }
    }
}