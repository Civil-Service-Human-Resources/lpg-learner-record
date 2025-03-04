package uk.gov.cslearning.record.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class CourseNotificationJobHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dataAcquisition;

    public CourseNotificationJobHistory(String name, LocalDateTime startedAt) {
        this.name = name;
        this.startedAt = startedAt;
    }

    public enum JobName {
        INCOMPLETED_COURSES_JOB
    }
}
