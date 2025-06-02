package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {
    @Query("SELECT mr FROM ModuleRecord mr WHERE mr.courseRecord.identity.userId in (?1) AND (?2 is NULL or mr.moduleId in (?2))")
    List<ModuleRecord> findByUserIdAndModuleIdIn(List<String> userIds, List<String> moduleIds);

    List<ModuleRecord> findAllByUpdatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<ModuleRecord> findAllByUpdatedAtBetweenAndCourseRecord_Identity_UserIdInOrderByCourseRecord_Identity_UserId(LocalDateTime from, LocalDateTime to, List<String> learnerIds);

    List<ModuleRecord> findAllByUpdatedAtBetweenAndCourseRecord_Identity_CourseIdInOrderByCourseRecord_Identity_UserId(LocalDateTime from, LocalDateTime to, List<String> courseIds);

    @Query("SELECT mr FROM ModuleRecord mr WHERE mr.courseRecord.identity.userId = ?1 AND mr.moduleId = ?2")
    Optional<ModuleRecord> getModuleRecord(String userId, String moduleId);
}
