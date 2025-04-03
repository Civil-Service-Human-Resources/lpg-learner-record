package uk.gov.cslearning.record.domain.record.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.record.domain.record.LearnerRecord;

import java.time.Instant;


@Entity
@Table(name = "learner_record_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecordEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_record_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_learner_events_record"))
    private LearnerRecord learnerRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_record_event_type", nullable = false,
            foreignKey = @ForeignKey(name = "fk_learner_events_type"))
    private LearnerRecordEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_record_event_source", nullable = false,
            foreignKey = @ForeignKey(name = "fk_learner_events_source"))
    private LearnerRecordEventSource eventSource;

    @Column(name = "event_timestamp", nullable = false, updatable = false)
    private Instant eventTimestamp;

    public LearnerRecordEvent(LearnerRecord learnerRecord, LearnerRecordEventType eventType,
                              LearnerRecordEventSource eventSource, Instant eventTimestamp) {
        this.learnerRecord = learnerRecord;
        this.eventType = eventType;
        this.eventSource = eventSource;
        this.eventTimestamp = eventTimestamp;
    }
}
