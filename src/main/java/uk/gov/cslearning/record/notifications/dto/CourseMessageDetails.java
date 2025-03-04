package uk.gov.cslearning.record.notifications.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CourseMessageDetails {
    private final String courseTitle;
    private final String courseDate;
    private final String courseLocation;
    private final String costInPounds;
}
