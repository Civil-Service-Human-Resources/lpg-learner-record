package uk.gov.cslearning.record.api.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.domain.Preference;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseRecordInput {

    @ValidEnum(enumClass = State.class)
    @Enumerated(EnumType.STRING)
    private String state;

    @ValidEnum(enumClass = Preference.class)
    @Enumerated(EnumType.STRING)
    private String preference;

    private boolean isRequired;
}
