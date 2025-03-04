package uk.gov.cslearning.record.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;

import java.util.List;
import java.util.Map;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <T, R> Map<String, T> executeMapRequest(RequestEntity<R> request, ParameterizedTypeReference<Map<String, T>> parameterizedTypeReference);

    <T, R> List<T> executeListRequest(RequestEntity<R> request, ParameterizedTypeReference<List<T>> parameterizedTypeReference);
}
