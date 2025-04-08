package uk.gov.cslearning.record.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;

import java.util.Map;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <T, R> T executeTypeRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference);

    <T, R> Map<String, T> executeMapRequest(RequestEntity<R> request, ParameterizedTypeReference<Map<String, T>> parameterizedTypeReference);
}
