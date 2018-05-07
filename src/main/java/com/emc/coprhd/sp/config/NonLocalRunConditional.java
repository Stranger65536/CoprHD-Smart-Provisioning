package com.emc.coprhd.sp.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static java.util.Arrays.asList;

public class NonLocalRunConditional implements Condition {
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        return !asList(context.getEnvironment().getActiveProfiles()).contains("local");
    }
}
