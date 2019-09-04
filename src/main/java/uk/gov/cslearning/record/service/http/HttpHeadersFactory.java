package uk.gov.cslearning.record.service.http;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpHeadersFactory {
    HttpHeaders authorizationHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return headers;
    }
}
