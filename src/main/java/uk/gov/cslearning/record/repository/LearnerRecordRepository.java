package uk.gov.cslearning.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.record.LearnerRecord;

import java.util.List;

@Repository
public interface LearnerRecordRepository extends JpaRepository<LearnerRecord, Long> {

    @Query("""
                select lr
                from LearnerRecord lr
                where (?1 is null or lr.learnerId = ?1)
                and (?2 is null or lr.resourceId = ?2)
                and (?3 is null or lr.learnerRecordType.id in (?3))
            """)
    Page<LearnerRecord> find(String userId, String resourceId, List<Integer> typeIds, Pageable pageable);

}
