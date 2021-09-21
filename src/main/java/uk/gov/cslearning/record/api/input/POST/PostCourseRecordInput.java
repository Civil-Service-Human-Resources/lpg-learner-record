package uk.gov.cslearning.record.api.input.POST;

import lombok.Data;
import uk.gov.cslearning.record.domain.Preference;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.validation.annotations.ModuleRecordMatchesCourseRecord;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ModuleRecordMatchesCourseRecord
public class PostCourseRecordInput {

    @NotBlank(message = "courseId is required")
    private String courseId;

    @NotBlank(message = "userId is required")
    private String userId;

    @ValidEnum(enumClass = State.class)
    @Enumerated(EnumType.STRING)
    private String state;

    @ValidEnum(enumClass = Preference.class)
    @Enumerated(EnumType.STRING)
    private String preference;

    @NotBlank(message = "courseTitle is required")
    private String courseTitle;

    private Boolean isRequired = false;

    @Size(min=1, message = "At least 1 module record is required to create a new course record")
    @Valid
    @NotNull(message = "moduleRecords is required")
    private List<PostModuleRecordInput> moduleRecords;
}
