package uk.gov.cslearning.record.api.input.POST;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PostModuleRecordInput {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "courseId is required")
    private String courseId;

    @NotBlank(message = "moduleId is required")
    private String moduleId;

    @NotBlank(message = "ModuleTitle is required")
    private String moduleTitle;

    private BigDecimal cost;

    @NotNull(message = "optional is required")
    private Boolean optional;

    @NotBlank(message = "moduleType is required")
    private String moduleType;

    @NotNull(message = "duration is required")
    private Long duration;

    @ValidEnum(enumClass = State.class)
    @Enumerated(EnumType.STRING)
    private String state;
}
