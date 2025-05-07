package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.ModuleRecordDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {

    @Query("SELECT mr FROM ModuleRecord mr WHERE mr.courseRecord.identity.userId in (?1) AND (?2 is NULL or mr.moduleId in (?2))")
    List<ModuleRecord> findByUserIdAndModuleIdIn(List<String> userIds, List<String> moduleIds);

    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.uid, mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord IS NOT NULL")
    List<ModuleRecordDto> findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.uid, mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.userId in (?3) " +
            "AND mr.courseRecord IS NOT NULL " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecordDto> findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> learnerIds);

    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.uid, mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.identity.courseId = mr.courseRecord.identity.courseId " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.courseId in (?3) " +
            "AND mr.courseRecord IS NOT NULL " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecordDto> findForCourseIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> courseIds);

    @Query("SELECT mr FROM ModuleRecord mr WHERE mr.courseRecord.identity.userId = ?1 AND mr.moduleId = ?2")
    Optional<ModuleRecord> getModuleRecord(String userId, String moduleId);
}
