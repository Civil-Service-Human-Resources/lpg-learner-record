package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.record.client.IHttpClient;

@Configuration
public class CivilServantRegistryClientConfig {
    private final OAuthClientFactory oAuthClientFactory;
    @Value("${registry-service.serviceUrl}")
    private String civilServantRegistryServiceBaseUrl;

    public CivilServantRegistryClientConfig(OAuthClientFactory oAuthClientFactory) {
        this.oAuthClientFactory = oAuthClientFactory;
    }

    @Bean(name = "civilServantRegistryHttpClient")
    IHttpClient civilServantRegistryClient() {
        return oAuthClientFactory.build(civilServantRegistryServiceBaseUrl);
    }
}
