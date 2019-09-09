package uk.gov.cslearning.record.service.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);
    private final RestTemplate restTemplate;
    private final RequestEntityFactoryHttp requestEntityFactoryHttp;
    private final ParameterizedTypeReferenceFactory parameterizedTypeReferenceFactory;

    public HttpService(RestTemplate restTemplate, RequestEntityFactoryHttp requestEntityFactoryHttp, ParameterizedTypeReferenceFactory parameterizedTypeReferenceFactory) {
        this.restTemplate = restTemplate;
        this.requestEntityFactoryHttp = requestEntityFactoryHttp;
        this.parameterizedTypeReferenceFactory = parameterizedTypeReferenceFactory;
    }

    public <T> Map<String, T> getMap(String uri, Class<T> type) {
        RequestEntity requestEntity = buildRequest(uri);
        LOGGER.debug(String.format("GET %s", uri));
        ResponseEntity<Map<String, T>> response = restTemplate.exchange(requestEntity,
                parameterizedTypeReferenceFactory.createMapReference(type)
        );

        return response.getBody();
    }

    public <T> Map<String, List<T>> getMapOfList(String uri, Class<T> type) {
        RequestEntity requestEntity = buildRequest(uri);
        LOGGER.debug(String.format("GET %s", uri));
        ResponseEntity<Map<String, List<T>>> response = restTemplate.exchange(requestEntity,
                parameterizedTypeReferenceFactory.createMapReferenceWithList(type)
        );

        return response.getBody();
    }

    private RequestEntity buildRequest(String uriString) {
        URI uri = URI.create(uriString);

        return requestEntityFactoryHttp.createGetRequest(uri);
    }
}
