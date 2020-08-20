package uk.gov.cslearning.record.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
        COMPLETED_COURSES_JOB,
        INCOMPLETED_COURSES_JOB
    }
}
