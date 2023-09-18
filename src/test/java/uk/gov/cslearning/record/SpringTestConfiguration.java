package uk.gov.cslearning.record;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapperImpl;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapperImpl;
import uk.gov.cslearning.record.api.output.error.GenericErrorResponseFactory;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;

import java.time.Clock;
import java.time.Instant;
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
    public PatchHelper patchHelper() {
        return new PatchHelper();
    }

    @Bean
    public CourseRecordMapper courseRecordMapper() { return new CourseRecordMapperImpl();
    }

    @Bean
    public ModuleRecordMapper moduleRecordMapper() { return new ModuleRecordMapperImpl();
    }

    @Bean
    public Clock clock() {return Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
    }

}
