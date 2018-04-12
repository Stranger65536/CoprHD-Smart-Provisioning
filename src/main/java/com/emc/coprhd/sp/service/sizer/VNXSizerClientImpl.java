package com.emc.coprhd.sp.service.sizer;

import com.emc.coprhd.sp.transfer.vnx.sizer.request.VNXSizerRequest;
import com.emc.coprhd.sp.transfer.vnx.sizer.response.VNXSizerResponse;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.xml.ws.http.HTTPException;

import static com.google.common.base.Preconditions.checkNotNull;

//@Service
public class VNXSizerClientImpl implements VNXSizerClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(VNXSizerClientImpl.class);

    private final String mainURL;
    private final Integer timeout;
    private final String fallbackURL;
    private final String calculateEndpoint;
    private final String checkEndpoint;

    private String url;
    private RestOperations restTemplate;

    @Autowired
    public VNXSizerClientImpl(
            @Value("${components.sizer.basic.url}") final String mainURL,
            @Value("${components.sizer.fallback.url}") final String fallbackURL,
            @Value("${components.sizer.timeout}") final String timeout,
            @Value("${components.sizer.endpoint.calculate}") final String calculateEndpoint,
            @Value("${components.sizer.endpoint.check}") final String checkEndpoint) {
        this.mainURL = checkNotNull(mainURL, "Main URL must be specified!");
        this.timeout = checkNotNull(Ints.tryParse(timeout), "Timeout must be specified!");
        this.fallbackURL = checkNotNull(fallbackURL, "Fallback URL must be specified!");
        this.calculateEndpoint = checkNotNull(calculateEndpoint, "Calculate endpoint must be specified!");
        this.checkEndpoint = checkNotNull(checkEndpoint, "Check endpoint must be specified!");
        this.url = this.mainURL;
    }

    @Override
    @SuppressWarnings("ProhibitedExceptionThrown")
    public VNXSizerResponse processSizerRequest(final VNXSizerRequest request) {
        final HttpEntity<VNXSizerRequest> httpRequest = new HttpEntity<>(request, prepareHTTPHeaders());
        try {
            return extractResponseData(restTemplate.exchange(url + calculateEndpoint,
                    HttpMethod.POST, httpRequest, VNXSizerResponse.class));
        } catch (Exception e) {
            if (url.equals(fallbackURL)) {
                throw e;
            } else {
                LOGGER.warn("Fatal error, switching to fallback VNX Sizer client", e);
                switchToFallback();
                return extractResponseData(restTemplate.exchange(url + calculateEndpoint,
                        HttpMethod.POST, httpRequest, VNXSizerResponse.class));
            }
        }
    }

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate(clientHttpRequestFactory());

        try {
            final HttpStatus code;
            if ((code = restTemplate.getForEntity(mainURL + checkEndpoint, String.class).getStatusCode())
                    == HttpStatus.OK) {
                LOGGER.info("Check of main VNX Sizer component successfully done!");
            } else {
                LOGGER.warn("Check of main VNX Sizer endpoint failed, switching to fallback! " +
                        "Response code was: {}", code);
                switchToFallback();
            }
        } catch (RestClientException e) {
            LOGGER.warn("Communication with main VNX Sizer endpoint failed, switching to fallback! Error was:", e);
            switchToFallback();
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        return factory;
    }

    private void switchToFallback() {
        this.url = fallbackURL;
    }

    private static HttpHeaders prepareHTTPHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static VNXSizerResponse extractResponseData(final ResponseEntity<VNXSizerResponse> responseEntity) {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new HTTPException(responseEntity.getStatusCode().value());
        }
    }
}