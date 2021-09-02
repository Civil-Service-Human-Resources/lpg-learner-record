package uk.gov.cslearning.record.api.input;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.domain.Preference;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class CourseRecordInput {

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private Preference preference;

    private boolean isRequired;
}
