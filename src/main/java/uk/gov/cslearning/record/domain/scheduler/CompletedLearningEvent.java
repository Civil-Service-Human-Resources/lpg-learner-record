package uk.gov.cslearning.record.domain.scheduler;

import lombok.Data;
import uk.gov.cslearning.record.domain.CourseRecord;

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
