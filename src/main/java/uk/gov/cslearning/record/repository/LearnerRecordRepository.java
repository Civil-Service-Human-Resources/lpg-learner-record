package uk.gov.cslearning.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.record.LearnerRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearnerRecordRepository extends JpaRepository<LearnerRecord, Long> {

    @Query("""
                select lr
                from LearnerRecord lr
                where (?1 is null or lr.learnerId in (?1))
                and (?2 is null or lr.resourceId in (?2))
                and (?3 is null or lr.learnerRecordType.recordType in (?3))
            """)
    Page<LearnerRecord> find(List<String> userIds, List<String> resourceIds, List<String> types, Pageable pageable);

    @Query("""
                select distinct lr
                from LearnerRecord lr
                where not exists (
                    select 1 from LearnerRecordEvent lre
                    where lre.eventType.id in (:notEventTypes)
                    and lre.learnerRecord = lr
                )
                and (:userIds is null or lr.learnerId in (:userIds))
                and (:resourceIds is null or lr.resourceId in (:resourceIds))
                and (:types is null or lr.learnerRecordType.recordType in (:types))
            """)
    Page<LearnerRecord> findExcludeByEventTypes(List<String> userIds, List<String> resourceIds, List<String> types, List<Integer> notEventTypes, Pageable pageable);


    @Query("""
                select lr
                from LearnerRecord lr
                where lr.learnerId = :userId
                and lr.resourceId = :resourceId
                and lr.learnerRecordType.recordType = :type
            """)
    Optional<LearnerRecord> find(String userId, String resourceId, String type);

}
