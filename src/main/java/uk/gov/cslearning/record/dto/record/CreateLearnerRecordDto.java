package uk.gov.cslearning.record.dto.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateLearnerRecordDto {
    @NotNull
    @Null(groups = {CourseRecordController.class})
    private Integer recordType;
    @NotNull
    private String resourceId;
    @NotNull
    private String learnerId;
    @Null(groups = {CourseRecordController.class})
    private Long parentId;
    private LocalDateTime createdTimestamp;

    @Valid
    @Size(max = 20, groups = {CourseRecordController.class})
    private List<CreateLearnerRecordDto> children = new ArrayList<>();

    @Size(max = 20)
    @Valid
    private List<CreateLearnerRecordEventDto> events = new ArrayList<>();

}
