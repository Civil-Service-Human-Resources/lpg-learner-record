package uk.gov.cslearning.record.domain.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "learner_records",
        uniqueConstraints = @UniqueConstraint(name = "uk_learner_record", columnNames = {"learner_record_type", "learner_id", "resource_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearnerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_record_type", nullable = false,
            foreignKey = @ForeignKey(name = "fk_learner_records_type"))
    private LearnerRecordType learnerRecordType;

    @Column(name = "learner_record_uid", length = 50)
    private String learnerRecordUid;

    @Column(name = "learner_id", length = 50, nullable = false)
    private String learnerId;

    @Column(name = "resource_id", length = 50, nullable = false)
    private String resourceId;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private Instant createdTimestamp;

    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_record_id",
            foreignKey = @ForeignKey(name = "fk_learner_records_parent"))
    private LearnerRecord parentRecord;
    @OneToMany(mappedBy = "parentRecord", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<LearnerRecord> childRecords = new ArrayList<>();
    @OneToMany(mappedBy = "learnerRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearnerRecordEvent> events = new ArrayList<>();

    public LearnerRecord(LearnerRecordType learnerRecordType, String learnerRecordUid, String learnerId,
                         String resourceId, Instant createdTimestamp) {
        this.learnerRecordType = learnerRecordType;
        this.learnerRecordUid = learnerRecordUid;
        this.learnerId = learnerId;
        this.resourceId = resourceId;
        this.createdTimestamp = createdTimestamp;
    }

    public Long getParentId() {
        return this.getParentRecord() != null ? this.getParentRecord().getParentId() : null;
    }

    @JsonIgnore
    public void addEvent(LearnerRecordEvent event) {
        this.events.add(event);
    }
}
