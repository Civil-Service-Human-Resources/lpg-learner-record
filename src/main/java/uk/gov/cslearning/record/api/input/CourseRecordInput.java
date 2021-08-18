package uk.gov.cslearning.record.api.input;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class CourseRecordInput {

    @Enumerated(EnumType.STRING)
    private State state;

    private String preference;

    private String profession;

    private String department;

    private LocalDateTime lastUpdated;

    private String courseTitle;

    private boolean isRequired;
}
