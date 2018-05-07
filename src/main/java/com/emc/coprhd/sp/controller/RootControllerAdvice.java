/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;

@ControllerAdvice
class RootControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootControllerAdvice.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleConflict(final HttpServletRequest req, final Exception e) {
        LOGGER.error("Request processing failed", e);
        return asJson(e);
    }

    private static String asJson(final Object obj) {
        final StringWriter w = new StringWriter();
        try {
            new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValue(w, obj);
        } catch (IOException ignored) {
            return null;
        }
        return w.toString();
    }
}