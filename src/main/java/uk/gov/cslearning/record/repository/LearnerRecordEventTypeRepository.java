package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEventType;

@Repository
public interface LearnerRecordEventTypeRepository extends JpaRepository<LearnerRecordEventType, Integer> {

}
