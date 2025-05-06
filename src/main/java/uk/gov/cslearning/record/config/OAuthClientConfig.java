package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.client.HttpClient;
import uk.gov.cslearning.record.client.IHttpClient;

@Configuration
public class OAuthClientConfig {
    @Value("${oauth.serviceUrl}")
    private String oAuthServiceUrl;

    @Value("${oauth.clientId}")
    private String identityClientId;

    @Value("${oauth.clientSecret}")
    private String identityClientSecret;

    @Bean(name = "oAuthClient")
    IHttpClient oAuthClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(oAuthServiceUrl)
                .basicAuthentication(identityClientId, identityClientSecret)
                .build();

        return new HttpClient(restTemplate);
    }


}
