package uk.gov.cslearning.record.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String courseId;

    @Column(nullable = false)
    private String identityUid;

    private LocalDateTime sent;

    private String notificationType;

    private static final String COMPLETED = "COMPLETED";

    private static final String NO_TYPE = "";


    public Notification() {
    }

    public Notification(String courseId, String identityUid, String notificationType) {
        checkArgument(courseId != null);
        checkArgument(identityUid != null);
        checkArgument(notificationType != null);

        this.courseId = courseId;
        this.sent = LocalDateTime.now();
        this.identityUid = identityUid;
        this.notificationType = notificationType;
    }

    public Notification(String courseId, String identityUid) {
        checkArgument(courseId != null);
        checkArgument(identityUid != null);

        this.courseId = courseId;
        this.sent = LocalDateTime.now();
        this.identityUid = identityUid;
        this.notificationType = NO_TYPE;
    }


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentityUid() {
        return identityUid;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("courseId", courseId)
                .append("identityId", identityUid)
                .append("notificationType", notificationType)
                .toString();
    }
}
