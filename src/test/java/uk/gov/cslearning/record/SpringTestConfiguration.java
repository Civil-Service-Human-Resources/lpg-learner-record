package uk.gov.cslearning.record;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponseFactory;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@TestConfiguration
public class SpringTestConfiguration {

    @Bean
    public ErrorDtoFactory errorDtoFactory() {
        return new ErrorDtoFactory();
    }

    @Bean
    public GenericErrorResponseFactory genericErrorResponseFactory() {
        return new GenericErrorResponseFactory();
    }

    @Bean
    public IUtilService stringUtilService() {
        return new IUtilService() {
            @Override
            public String generateUUID() {
                return "UUID";
            }

            @Override
            public LocalDateTime getNowDateTime() {
                return LocalDateTime.now(clock());
            }
        };
    }

    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
    }

}
