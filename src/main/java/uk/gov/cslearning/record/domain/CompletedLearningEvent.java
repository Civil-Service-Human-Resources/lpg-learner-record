package uk.gov.cslearning.record.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class CompletedLearningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private CourseRecord courseRecord;

    private Instant completedOn;

    public CompletedLearningEvent() {
    }

    public CompletedLearningEvent(CourseRecord courseRecord, Instant completedOn) {
        this.courseRecord = courseRecord;
        this.completedOn = completedOn;
    }
}