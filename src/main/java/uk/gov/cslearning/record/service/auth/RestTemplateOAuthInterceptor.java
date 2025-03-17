package uk.gov.cslearning.record.service.auth;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestTemplateOAuthInterceptor implements ClientHttpRequestInterceptor {

    private final IBearerTokenService bearerTokenService;

    public RestTemplateOAuthInterceptor(IBearerTokenService bearerTokenService) {
        this.bearerTokenService = bearerTokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = bearerTokenService.getBearerToken();
        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }
}
