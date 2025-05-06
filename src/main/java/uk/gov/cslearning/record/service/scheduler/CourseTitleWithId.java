package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CourseTitleWithId {

    protected final String courseId;
    protected final String courseTitle;

    public CourseTitleWithId(CoursePartial partial) {
        this(partial.getCourseId(), partial.getCourseTitle());
    }

}
