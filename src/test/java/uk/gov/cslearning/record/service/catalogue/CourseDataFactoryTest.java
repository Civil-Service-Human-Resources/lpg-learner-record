package uk.gov.cslearning.record.service.catalogue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CourseDataFactoryTest {
    private final Audience audience = new Audience();
    @Mock
    private IUtilService iUtilService;
    @InjectMocks
    private CourseDataFactory courseDataFactory;

    @BeforeEach
    public void before() {
        when(iUtilService.getNowDateTime()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 0));
    }

    @Test
    public void testGenerateLearningPeriodWithYearFrequency() {
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2023, 6, 10));
        LearningPeriod result = courseDataFactory.getLearningPeriod(audience);
        assertEquals(LocalDate.of(2022, 6, 10), result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithPastDueDate() {
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2021, 5, 10));
        LearningPeriod result = courseDataFactory.getLearningPeriod(audience);
        assertEquals(LocalDate.of(2022, 5, 10), result.getStartDate());
        assertEquals(LocalDate.of(2023, 5, 10), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithNoFrequency() {
        audience.setFrequency(null);
        audience.setRequiredBy(LocalDate.of(2023, 6, 10));
        LearningPeriod result = courseDataFactory.getLearningPeriod(audience);
        assertEquals(null, result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10), result.getEndDate());
    }

}
