package uk.gov.cslearning.record.domain.scheduler;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
public class LineManagerRequiredLearningNotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lineManagerUsername;
    private String lineManagerUid;
    private String name;
    private String uid;
    private String courseId;
    private String courseTitle;
    private Instant createdAt;

    public LineManagerRequiredLearningNotificationEvent() {
    }

    public LineManagerRequiredLearningNotificationEvent(String lineManagerUsername, String lineManagerUid, String name, String uid, String courseId, String courseTitle, Instant createdAt) {
        this.lineManagerUsername = lineManagerUsername;
        this.lineManagerUid = lineManagerUid;
        this.name = name;
        this.uid = uid;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.createdAt = createdAt;
    }
}