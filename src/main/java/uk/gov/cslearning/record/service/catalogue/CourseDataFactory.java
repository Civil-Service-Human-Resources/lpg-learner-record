package uk.gov.cslearning.record.service.catalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseDataFactory {

    private final IUtilService utilService;

    public LearningPeriod getLearningPeriod(Audience audience) {
        log.debug(String.format("Building learning period for audience with departments: %s", audience.getDepartments()));
        LocalDateTime requiredByAsDateTime = audience.getRequiredBy().atTime(LocalTime.MAX);
        log.debug(String.format("Required by: %s", requiredByAsDateTime));
        return audience.getFrequencyAsPeriod().map(frequency -> {
            log.debug(String.format("Frequency is: Years: %s | Months: %s | Days: %s", frequency.getYears(), frequency.getMonths(), frequency.getDays()));
            LocalDateTime now = utilService.getNowDateTime();
            log.debug(String.format("Time now is: %s", now));
            LocalDateTime endDate = requiredByAsDateTime;
            while (endDate.isBefore(now)) {
                endDate = endDate.plus(frequency);
            }
            LocalDateTime startDate = endDate.minus(frequency);
            log.debug(String.format("Start date is %s, end date is %s", startDate, endDate));
            return new LearningPeriod(startDate.toLocalDate(), endDate.toLocalDate());
        }).orElseGet(() -> {
            log.debug("No frequency data found, setting start date as EPOCH");
            return new LearningPeriod(null, requiredByAsDateTime.toLocalDate());
        });
    }

    public RequiredCourse transformCourse(Course course) {
        Map<String, LearningPeriod> learningPeriodMap = new HashMap<>();
        course.getAudiences().forEach(a -> {
            LearningPeriod learningPeriod = getLearningPeriod(a);
            a.getDepartments().forEach(dep -> {
                learningPeriodMap.putIfAbsent(dep, learningPeriod);
            });
        });
        return new RequiredCourse(course.getId(), course.getTitle(), course.getModules(), course.getAudiences(),
                learningPeriodMap);
    }

}
