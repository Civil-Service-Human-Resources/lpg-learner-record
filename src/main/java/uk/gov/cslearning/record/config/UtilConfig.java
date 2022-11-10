package uk.gov.cslearning.record.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UtilConfig {

    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}
