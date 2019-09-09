package uk.gov.cslearning.record;

import gov.adlnet.xapi.client.StatementClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public StatementClient statementClient(
            @Value("${xapi.url}") String url,
            @Value("${xapi.username}") String username,
            @Value("${xapi.password}") String password
    ) throws MalformedURLException {
        return new StatementClient(url, username, password);
    }

    @Bean
    @ConfigurationProperties("oauth.client")
    protected ClientCredentialsResourceDetails oAuthDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @Qualifier("clientRestTemplate")
    protected RestTemplate restTemplate() {
        return new OAuth2RestTemplate(oAuthDetails());
    }
}
