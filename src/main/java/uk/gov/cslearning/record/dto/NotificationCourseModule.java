package uk.gov.cslearning.record.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.service.catalogue.Course;

@Getter
@Setter
@AllArgsConstructor
public class NotificationCourseModule {
    private CivilServant civilServant;
    private List<Course> courses;
}
