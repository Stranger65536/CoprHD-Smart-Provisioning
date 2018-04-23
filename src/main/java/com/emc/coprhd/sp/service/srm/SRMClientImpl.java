package com.emc.coprhd.sp.service.srm;

import com.emc.coprhd.sp.service.common.AbstractFallBackable;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class SRMClientImpl extends AbstractFallBackable<SRMClient> implements SRMClient {
    private final String url;
    private final int timeout;
    private final RestOperations restTemplate;

    @Autowired
    public SRMClientImpl(
            @Value("${com.emc.coprhd.sp.srm.url}") final String url,
            @Value("${com.emc.coprhd.sp.srm.timeout}") final int timeout) {
        this.url = url;
        this.timeout = timeout;
        this.restTemplate = new RestTemplate(clientHttpRequestFactory());
    }

    @Override
    public SRMPoolInfo getStoragePoolInfoByName(final String poolName) {
        final ResponseEntity<SRMPoolInfo> responseEntity = restTemplate
                .getForEntity(url + "?pool={pool}",
                        SRMPoolInfo.class, poolName);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RestClientException("Non-zero response!" + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    @Override
    public void initialize() {
        getStoragePoolInfoByName("test");
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        return factory;
    }
}
