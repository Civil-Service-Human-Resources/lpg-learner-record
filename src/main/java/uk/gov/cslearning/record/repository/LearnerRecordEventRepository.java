package uk.gov.cslearning.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.cslearning.record.domain.record.event.LearnerRecordEvent;

import java.time.Instant;
import java.util.List;

public interface LearnerRecordEventRepository extends JpaRepository<LearnerRecordEvent, Long> {

    @Query("""
                select lre
                from LearnerRecordEvent lre
                where (:learnerRecordId is null or lre.learnerRecord.id = :learnerRecordId)
                and (:learnerRecordEventTypeIds is null or lre.eventType.id in :learnerRecordEventTypeIds)
                and (:before is null or lre.eventTimestamp >= :before)
                and (:after is null or lre.eventTimestamp <= :after)
                and (:userId is null or lre.learnerRecord.learnerId = :userId)
            """)
    Page<LearnerRecordEvent> find(Long learnerRecordId, List<Integer> learnerRecordEventTypeIds, String userId,
                                  Instant before, Instant after, Pageable pageable);

}
