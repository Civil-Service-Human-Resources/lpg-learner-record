package uk.gov.cslearning.record.api.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Result;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ModuleRecordInput {

    @ValidEnum(enumClass = State.class)
    @Enumerated(EnumType.STRING)
    private String state;

    @ValidEnum(enumClass = Result.class)
    @Enumerated(EnumType.STRING)
    private String result;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    private String score;

    private boolean rated;

    private String paymentMethod;

    private String paymentDetails;

    @ValidEnum(enumClass = BookingStatus.class)
    @Enumerated(EnumType.STRING)
    private String bookingStatus;

}
