package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.service.catalogue.Audience;
import uk.gov.cslearning.record.service.catalogue.Course;

import java.time.LocalDate;

@Service
public class CourseService {

    public CourseService() {
    }

    public LocalDate getNextRequiredBy(CivilServantDto civilServant, LocalDate completionDate, Course course) {
        Audience audience = getMostRelevantAudienceFor(civilServant, course);
        if (audience != null) {
            return audience.getNextRequiredBy(completionDate);
        }
        return null;
    }

    private Audience getMostRelevantAudienceFor(CivilServantDto civilServant, Course course) {
        int highestRelevance = -1;
        Audience mostRelevantAudience = null;

        for (Audience audience : course.getAudiences()) {
            int audienceRelevance = audience.getRelevance(civilServant);
            if (audienceRelevance > highestRelevance) {
                mostRelevantAudience = audience;
                highestRelevance = audienceRelevance;
            }
        }
        if (highestRelevance > -1) {
            return mostRelevantAudience;
        }
        return null;
    }
}
