/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.sizer;

import com.emc.coprhd.sp.exception.StartupException;
import com.emc.coprhd.sp.transfer.vnx.sizer.request.VNXSizerRequest;
import com.emc.coprhd.sp.transfer.vnx.sizer.response.VNXSizerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static com.emc.coprhd.sp.util.RuntimeUtils.enterMethodMessage;
import static com.emc.coprhd.sp.util.RuntimeUtils.exitMethodMessage;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class VNXSizerClientImpl implements VNXSizerClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(VNXSizerClientImpl.class);

    private final String url;
    private final Integer timeout;
    private final HttpHeaders headers;
    private final ObjectMapper mapper;
    private final RestOperations restTemplate;
    private final String vnxSizerTemplateFile;

    @Autowired
    public VNXSizerClientImpl(
            final ObjectMapper mapper,
            @Value("${com.emc.coprhd.sp.sizer.url}") final String url,
            @Value("${com.emc.coprhd.sp.sizer.timeout}") final int timeout,
            @Value("${com.emc.coprhd.sp.sizer.template}") final String vnxSizerTemplateFile) {
        this.url = url;
        this.timeout = timeout;
        this.mapper = mapper;
        this.vnxSizerTemplateFile = vnxSizerTemplateFile;
        this.headers = prepareHTTPHeaders();
        this.restTemplate = new RestTemplate(clientHttpRequestFactory());
    }

    @Override
    @SuppressWarnings("ProhibitedExceptionThrown")
    public VNXSizerResponse processSizerRequest(final VNXSizerRequest request) {
        LOGGER.debug("{} request: {}", enterMethodMessage(), request);
        final HttpEntity<VNXSizerRequest> entity = new HttpEntity<>(request, headers);
        final ResponseEntity<VNXSizerResponse> responseEntity = restTemplate
                .postForEntity(url, entity, VNXSizerResponse.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RestClientException("Non-zero response!" + responseEntity.getStatusCode());
        }
        LOGGER.debug("{} response: {}, request: {}", exitMethodMessage(), responseEntity.getBody(), request);
        return responseEntity.getBody();
    }

    public void init() {
        try {
            final File requestTemplateFile = new File(checkNotNull(vnxSizerTemplateFile,
                    "JSON template path must be specified!"));
            checkState(requestTemplateFile.exists(), "JSON template file must exist!");
            final VNXSizerRequest request = mapper.readValue(requestTemplateFile, VNXSizerRequest.class);
            processSizerRequest(request);
        } catch (IOException e) {
            throw new StartupException("Can't get response from VNX Sizer", e);
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        return factory;
    }

    private static HttpHeaders prepareHTTPHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}