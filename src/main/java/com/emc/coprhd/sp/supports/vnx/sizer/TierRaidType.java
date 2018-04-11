/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.supports.vnx.sizer;

import com.emc.coprhd.sp.supports.vnx.sizer.TierRaidType.TierRaidTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = TierRaidTypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum TierRaidType {
    RAID_TYPE_10("RAID_TYPE_10"),
    RAID_TYPE_5("RAID_TYPE_5"),
    RAID_TYPE_6("RAID_TYPE_6"),
    RAID_TYPE_3("RAID_TYPE_3"),
    RAID_TYPE_0("RAID_TYPE_0"),
    RAID_TYPE_NONE("RAID_TYPE_NONE");

    final String value;

    TierRaidType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static TierRaidType forValue(final String value) {
        final TierRaidType[] values = values();
        for (final TierRaidType tierRaidType : values) {
            if (tierRaidType.value.equalsIgnoreCase(value)) {
                return tierRaidType;
            }
        }
        return null;
    }

    static class TierRaidTypeDeserializer extends JsonDeserializer<TierRaidType> {
        @Override
        public TierRaidType deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext) throws IOException {
            return forValue(jsonParser.getText());
        }
    }
}
