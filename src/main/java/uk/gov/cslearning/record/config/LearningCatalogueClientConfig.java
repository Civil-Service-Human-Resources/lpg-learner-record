package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.record.client.IHttpClient;

@Configuration
public class LearningCatalogueClientConfig {
    private final OAuthClientFactory oAuthClientFactory;
    @Value("${catalogue.serviceUrl}")
    private String learningCatalogueServiceBaseUrl;

    public LearningCatalogueClientConfig(OAuthClientFactory oAuthClientFactory) {
        this.oAuthClientFactory = oAuthClientFactory;
    }

    @Bean(name = "learningCatalogueHttpClient")
    IHttpClient learnerRecordClient() {
        return oAuthClientFactory.build(learningCatalogueServiceBaseUrl);
    }
}
