package uk.gov.cslearning.record.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;

@ConfigurationProperties(prefix = "time")
@Data
public class ClockConfig {

    private String zoneId;

    @Bean
    public Clock getClock() {
        if (StringUtils.isBlank(zoneId)) {
            return Clock.systemDefaultZone();
        }
        return Clock.system(ZoneId.of(zoneId.trim()));
    }
}
