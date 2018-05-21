package uk.gov.cslearning.record.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.HttpService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.scheduler.LearningJob;
import uk.gov.cslearning.record.service.xapi.XApiService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpService httpService;

    @Autowired
    private XApiProperties xApiProperties;

    @Autowired
    private XApiService xApiService;

    @Bean
    public LearningJob learningJob() {
        return new LearningJob(httpService);
    }

    @Bean
    public HttpService http() {
        return new HttpService(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public XApiProperties xApiProperties(){
        return new XApiProperties();
    }

    @Bean
    public XApiService xApiService(){
        return new XApiService(xApiProperties);
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
}
