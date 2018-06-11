package uk.gov.cslearning.record.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseId;

    @Column(nullable = false)
    private String identityUid;

    @Column(nullable = false)
    private LocalDateTime sent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification() {
    }

    public Notification(String courseId, String identityUid, NotificationType type) {
        checkArgument(courseId != null);
        checkArgument(identityUid != null);
        checkArgument(type != null);
        this.courseId = courseId;
        this.sent = LocalDateTime.now();
        this.identityUid = identityUid;
        this.type = type;
    }

    public String getCourseId() {
        return courseId;
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

    public String getIdentityUid() {
        return identityUid;
    }

    public NotificationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("courseId", courseId)
                .append("identityId", identityUid)
                .append("type", type)
                .toString();
    }

    public boolean sentBefore(LocalDateTime date) {
        return sent.isBefore(date);
    }
}
