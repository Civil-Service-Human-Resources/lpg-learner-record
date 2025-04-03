package uk.gov.cslearning.record.dto.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateLearnerRecordDto {
    @NotNull
    private Integer recordType;
    @NotNull
    private String resourceId;
    @NotNull
    private String learnerId;
    private Long parentId;
    private Instant createdTimestamp;

    @Size(max = 20)
    @Valid
    private List<CreateLearnerRecordDto> children;

    @Size(max = 20)
    @Valid
    private List<CreateLearnerRecordEventDto> events;

    public List<CreateLearnerRecordDto> getChildren() {
        return children == null ? new ArrayList<>() : children;
    }

    public List<CreateLearnerRecordEventDto> getEvents() {
        return events == null ? new ArrayList<>() : events;
    }
}
