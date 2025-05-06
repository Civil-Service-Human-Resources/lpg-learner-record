package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.record.client.IHttpClient;

@Configuration
public class NotificationClientConfig {
    private final OAuthClientFactory oAuthClientFactory;
    @Value("${notify.serviceUrl}")
    private String notificationServiceUrl;

    public NotificationClientConfig(OAuthClientFactory oAuthClientFactory) {
        this.oAuthClientFactory = oAuthClientFactory;
    }

    @Bean(name = "notificationHttpClient")
    IHttpClient notificationClient() {
        return oAuthClientFactory.build(notificationServiceUrl);
    }


}
