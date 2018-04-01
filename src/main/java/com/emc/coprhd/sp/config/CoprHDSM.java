/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
/*
 * YEP
 */
package com.emc.coprhd.sp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.emc.coprhd.sp")
@SuppressWarnings({"UtilityClassCanBeEnum", "NonFinalUtilityClass"})
public class CoprHDSM {
    public static void main(final String[] args) {
        SpringApplication.run(CoprHDSM.class);
    }
}
