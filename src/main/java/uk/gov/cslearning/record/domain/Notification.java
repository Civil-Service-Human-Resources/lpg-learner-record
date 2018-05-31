package uk.gov.cslearning.record.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String identityUid;

    private LocalDateTime sent;

    public Notification(String courseId, String identityUid) {
        checkArgument(courseId != null);
        checkArgument(identityUid != null);

        this.courseId = courseId;
        this.sent = LocalDateTime.now();
        this.identityUid = identityUid;
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
}
