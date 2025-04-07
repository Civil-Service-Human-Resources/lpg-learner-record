package uk.gov.cslearning.record.domain.record.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.record.domain.record.LearnerRecordType;

@Entity
@Table(name = "learner_record_event_types",
        uniqueConstraints = @UniqueConstraint(name = "uk_learner_event_type", columnNames = {"record_type", "event_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecordEventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_type", nullable = false,
            foreignKey = @ForeignKey(name = "fk_learner_event_types_record_type"))
    private LearnerRecordType recordType;

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType;

    @Column(name = "description", nullable = false)
    private String description;
    
}
