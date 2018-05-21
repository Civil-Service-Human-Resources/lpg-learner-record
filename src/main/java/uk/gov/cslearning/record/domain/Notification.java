package uk.gov.cslearning.record.domain;

import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

public class Notification {

    private String courseId;

    private String userId;

    private LocalDateTime sent;

    public Notification(String courseId, String userId) {
        checkArgument(courseId != null);
        checkArgument(userId != null);
        this.courseId = courseId;
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }
}
