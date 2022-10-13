package uk.gov.cslearning.record.api.input.PATCH;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.domain.Preference;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude
public class PatchCourseRecordInput {

    @ValidEnum(enumClass = State.class)
    @Enumerated(EnumType.STRING)
    private String state;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @ValidEnum(enumClass = Preference.class)
    @Enumerated(EnumType.STRING)
    private String preference;

    private boolean isRequired;
}
