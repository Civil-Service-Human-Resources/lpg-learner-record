package uk.gov.cslearning.record.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
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

    public Notification(String courseId, String identityUid, LocalDateTime sent, NotificationType type) {
        this.courseId = courseId;
        this.sent = sent;
        this.identityUid = identityUid;
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("courseId", courseId)
                .append("identityId", identityUid)
                .append("type", type)
                .toString();
    }
}
