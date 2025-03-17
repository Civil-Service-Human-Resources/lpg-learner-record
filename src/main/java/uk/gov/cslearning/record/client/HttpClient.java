package uk.gov.cslearning.record.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class HttpClient implements IHttpClient {

    private final RestTemplate restTemplate;

    @Override
    public <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            log.debug("Request response: {}", response);
            return response.getBody();
        } catch (RestClientResponseException e) {
            String msg = String.format("Error sending '%s' request to endpoint", request.getMethod());
            if (request.getBody() != null) {
                msg = String.format("%s Body was: %s.", msg, request.getBody().toString());
            }
            msg = String.format("%s Error was: %s", msg, e.getMessage());
            log.error(msg);
            throw e;
        }
    }

    public <T, R> Map<String, T> executeMapRequest(RequestEntity<R> request, ParameterizedTypeReference<Map<String, T>> ptr) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<Map<String, T>> response = restTemplate.exchange(request, ptr);
            log.debug("Request response: {}", response);
            return response.getBody();
        } catch (RestClientResponseException e) {
            String msg = String.format("Error sending '%s' request to endpoint", request.getMethod());
            if (request.getBody() != null) {
                msg = String.format("%s Body was: %s.", msg, request.getBody().toString());
            }
            msg = String.format("%s Error was: %s", msg, e.getMessage());
            log.error(msg);
            throw e;
        }
    }

    @Override
    public <T, R> List<T> executeListRequest(RequestEntity<R> request, ParameterizedTypeReference<List<T>> parameterizedTypeReference) {
        try {
            log.debug("Sending request: {}", request);
            ResponseEntity<List<T>> response = restTemplate.exchange(request, parameterizedTypeReference);

            log.debug("Request response: {}", response);
            return response.getBody();
        } catch (RestClientResponseException e) {
            String msg = String.format("Error sending '%s' request to endpoint", request.getMethod());
            if (request.getBody() != null) {
                msg = String.format("%s Body was: %s.", msg, request.getBody().toString());
            }
            msg = String.format("%s Error was: %s", msg, e.getMessage());
            log.error(msg);
            throw e;
        }
    }
}
