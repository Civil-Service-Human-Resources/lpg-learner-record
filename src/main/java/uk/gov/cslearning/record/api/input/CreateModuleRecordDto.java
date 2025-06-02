package uk.gov.cslearning.record.api.input;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.record.domain.State;

@NoArgsConstructor
@Getter
@Setter
public class CreateModuleRecordDto {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "courseId is required")
    private String courseId;

    @Column(nullable = false)
    @NotBlank(message = "moduleId is required")
    private String moduleId;

    @NotBlank(message = "ModuleTitle is required")
    private String moduleTitle;

    @NotBlank(message = "moduleType is required")
    private String moduleType;

    @NotNull(message = "optional is required")
    private Boolean optional = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @NotNull
    private State state;

}
