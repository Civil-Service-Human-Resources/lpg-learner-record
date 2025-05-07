package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {
    @Query("SELECT mr " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord IS NOT NULL")
    List<ModuleRecord> findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to);

    @Query("SELECT mr " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.userId in (?3) " +
            "AND mr.courseRecord IS NOT NULL " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecord> findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> learnerIds);

    @Query("SELECT mr " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.courseId in (?3) " +
            "AND mr.courseRecord IS NOT NULL " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecord> findForCourseIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> courseIds);
}
