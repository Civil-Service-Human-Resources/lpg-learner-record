package uk.gov.cslearning.record;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.*;

@Configuration
public class SpringTestConfiguration {

    @Bean
    @Primary
    public IUtilService testUtilService() {
        return new IUtilService() {
            @Override
            public String generateUUID() {
                return "UUID";
            }

            @Override
            public String generateSaltedString(int hashLength) {
                return "Rand1";
            }

            @Override
            public LocalDateTime getNowDateTime() {
                return LocalDateTime.now(clock());
            }

            @Override
            public Instant getNowInstant() {
                return getNowDateTime().toInstant(ZoneOffset.UTC);
            }
        };
    }

    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
    }

}
