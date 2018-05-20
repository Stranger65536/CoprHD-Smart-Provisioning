/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import com.emc.coprhd.sp.dto.ErrorJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.emc.coprhd.sp.controller.ContextPaths.ERROR;

@RestController
public class JsonErrorController implements ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonErrorController.class);
    private final ErrorAttributes errorAttributes;

    @Autowired
    public JsonErrorController(final ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERROR;
    }

    @RequestMapping(value = ERROR, produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unused")
    ErrorJson error(final HttpServletRequest request, final HttpServletResponse response) {
        return new ErrorJson(response.getStatus(), getErrorAttributes(request));
    }

    private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
        final RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        final Map<String, Object> attributes = errorAttributes.getErrorAttributes(requestAttributes, true);
        //noinspection ThrowableNotThrown
        final Throwable throwable = errorAttributes.getError(requestAttributes);
        LOGGER.error("Non-handler exception occurred: {}!", attributes, throwable);
        attributes.put("exception", errorAttributes.getError(requestAttributes));
        return attributes;
    }
}
