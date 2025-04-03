package uk.gov.cslearning.record.domain.record;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learner_record_types",
        uniqueConstraints = @UniqueConstraint(name = "uk_learner_record_types_type", columnNames = "record_type"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LearnerRecordType {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "record_type", length = 50, nullable = false)
    private String recordType;

    @OneToMany(mappedBy = "recordType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearnerRecordEventType> eventTypes = new ArrayList<>();

}
