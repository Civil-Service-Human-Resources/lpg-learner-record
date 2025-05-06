package uk.gov.cslearning.record.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventIds {

    private final String courseId;
    private final String moduleId;
    private final String eventId;

}
