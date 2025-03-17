package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.record.client.IHttpClient;

@Configuration
public class IdentitiesClientConfig {
    private final OAuthClientFactory oAuthClientFactory;
    @Value("${oauth.serviceUrl}")
    private String identityServiceBaseUrl;

    public IdentitiesClientConfig(OAuthClientFactory oAuthClientFactory) {
        this.oAuthClientFactory = oAuthClientFactory;
    }

    @Bean(name = "identitiesHttpClient")
    IHttpClient identitiesHttpClient() {
        return oAuthClientFactory.build(identityServiceBaseUrl);
    }
}
