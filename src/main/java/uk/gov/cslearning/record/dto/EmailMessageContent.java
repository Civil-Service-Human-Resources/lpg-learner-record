package uk.gov.cslearning.record.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.service.catalogue.Course;

@AllArgsConstructor
@Getter
public class EmailMessageContent {
    private CivilServant civilServant;
    private Course course;
    private LocalDateTime completedDate;
}
