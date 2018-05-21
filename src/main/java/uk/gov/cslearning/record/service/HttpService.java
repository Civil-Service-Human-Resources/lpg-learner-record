package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    private RestTemplate restTemplate;

    @Autowired
    public HttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object[] httpGet(String url) {
        LOGGER.debug("HTTP GET to {}", url);
        // request client token, use this token going forward

        Object[] response = restTemplate.getForObject(url, Object[].class);

        return response;
    }
}
