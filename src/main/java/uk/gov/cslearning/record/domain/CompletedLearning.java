package uk.gov.cslearning.record.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class CompletedLearning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private CourseRecord courseRecord;

    private Instant completedOn;

    public CompletedLearning(CourseRecord courseRecord, Instant completedOn) {
        this.courseRecord = courseRecord;
        this.completedOn = completedOn;
    }

    public CompletedLearning() {
    }
}
