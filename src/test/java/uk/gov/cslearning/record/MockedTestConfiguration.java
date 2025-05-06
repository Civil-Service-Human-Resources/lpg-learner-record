package uk.gov.cslearning.record;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponseFactory;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;

@Configuration
public class MockedTestConfiguration {

    @Bean
    public ErrorDtoFactory errorDtoFactory() {
        return new ErrorDtoFactory();
    }

    @Bean
    public GenericErrorResponseFactory genericErrorResponseFactory() {
        return new GenericErrorResponseFactory();
    }

}
