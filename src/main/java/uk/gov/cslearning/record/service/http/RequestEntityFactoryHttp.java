package uk.gov.cslearning.record.service.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class RequestEntityFactoryHttp {
    RequestEntity createGetRequest(HttpHeaders headers, URI uri) {
        return new RequestEntity(headers, HttpMethod.GET, uri);
    }
}
