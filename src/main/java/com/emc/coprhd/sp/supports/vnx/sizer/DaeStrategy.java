/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.supports.vnx.sizer;

import com.emc.coprhd.sp.supports.vnx.sizer.DaeStrategy.DaeStrategyDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = DaeStrategyDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum DaeStrategy {
    DAE_STRATEGY_NONE("DAE_STRATEGY_NONE"),
    DAE_STRATEGY_LO("DAE_STRATEGY_LO"),
    DAE_STRATEGY_HI("DAE_STRATEGY_HI"),
    DAE_STRATEGY_EFFMIX("DAE_STRATEGY_EFFMIX"),
    DAE_STRATEGY_VERYHI("DAE_STRATEGY_VERYHI");

    final String value;

    DaeStrategy(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    private static DaeStrategy forValue(final String value) {
        final DaeStrategy[] types = values();
        for (DaeStrategy poolType : types) {
            if (poolType.value.equalsIgnoreCase(value)) {
                return poolType;
            }
        }
        return null;
    }

    static class DaeStrategyDeserializer extends JsonDeserializer<DaeStrategy> {
        @Override
        public DaeStrategy deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext) throws IOException {
            return forValue(jsonParser.getText());
        }
    }
}