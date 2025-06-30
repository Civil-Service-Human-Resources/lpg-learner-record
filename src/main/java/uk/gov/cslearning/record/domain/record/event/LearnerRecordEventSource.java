package uk.gov.cslearning.record.domain.record.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "learner_record_event_sources",
        uniqueConstraints = @UniqueConstraint(name = "uk_learner_event_sources_source", columnNames = "source"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecordEventSource {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "source", length = 50, nullable = false)
    private String source;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "uid", nullable = false)
    private String uid;

}
