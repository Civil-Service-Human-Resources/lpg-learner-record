package uk.gov.cslearning.record.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.identity.IdentityService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.scheduler.LearningJob;
import uk.gov.cslearning.record.service.xapi.XApiService;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private XApiService xApiService;

    @Bean
    public LearningJob learningJob() {
        return new LearningJob(identityService);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserRecordService userRecordService(){
        return new UserRecordService(xApiService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("LPG");

        http.csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and().httpBasic()
                .authenticationEntryPoint(entryPoint);
    }

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails(OAuthProperties oAuthProperties) {

        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setId("identity");
        resource.setAccessTokenUri(oAuthProperties.getTokenUrl());
        resource.setClientId(oAuthProperties.getClientId());
        resource.setClientSecret(oAuthProperties.getClientSecret());

        return resource;
    }

    @Bean
    public OAuth2RestOperations oAuthRestTemplate(OAuth2ProtectedResourceDetails resourceDetails) {
        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(atr));
    }
}
