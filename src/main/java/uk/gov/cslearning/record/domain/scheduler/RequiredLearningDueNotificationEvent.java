package uk.gov.cslearning.record.domain.scheduler;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
public class RequiredLearningDueNotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identityUsername;
    private String identityUid;
    private String courseId;
    private String courseTitle;
    private String period;
    private Instant createdAt;

    public RequiredLearningDueNotificationEvent() {
    }

    public RequiredLearningDueNotificationEvent(String identityUsername, String identityUid, String courseId, String courseTitle, String period, Instant createdAt) {
        this.identityUsername = identityUsername;
        this.identityUid = identityUid;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.period = period;
        this.createdAt = createdAt;
    }
}
