package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {
    List<ModuleRecord> findAllByCreatedAtBetweenAndCourseRecordIsNotNull(LocalDateTime from, LocalDateTime to);
}
