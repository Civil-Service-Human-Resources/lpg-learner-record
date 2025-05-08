package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {
    List<ModuleRecord> findAllByUpdatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<ModuleRecord> findAllByUpdatedAtBetweenAndCourseRecord_Identity_UserIdIn(LocalDateTime from, LocalDateTime to, List<String> learnerIds);

    List<ModuleRecord> findAllByUpdatedAtBetweenAndCourseRecord_Identity_CourseIdIn(LocalDateTime from, LocalDateTime to, List<String> courseIds);
}
