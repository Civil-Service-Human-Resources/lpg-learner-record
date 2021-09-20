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
    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.id = mr.courseRecord.id " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord IS NOT EMPTY")
    List<ModuleRecordDto> findAllByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.id = mr.courseRecord.id " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.userId in (?3) " +
            "AND mr.courseRecord IS NOT EMPTY " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecordDto> findForLearnerIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> learnerIds);

    @Query("SELECT new uk.gov.cslearning.record.dto.ModuleRecordDto(mr.moduleId, mr.state, cr.identity.userId, mr.updatedAt,  mr.completionDate, mr.moduleTitle, mr.moduleType, mr.courseRecord.identity.courseId, mr.courseRecord.courseTitle) " +
            "FROM ModuleRecord mr " +
            "left join CourseRecord cr on cr.id = mr.courseRecord.id " +
            "WHERE mr.updatedAt BETWEEN ?1 AND ?2 " +
            "AND mr.courseRecord.identity.courseId in (?3) " +
            "AND mr.courseRecord IS NOT EMPTY " +
            "ORDER BY mr.courseRecord.identity.userId")
    List<ModuleRecordDto> findForCourseIdsByCreatedAtBetweenAndCourseRecordIsNotNullNormalised(LocalDateTime from, LocalDateTime to, List<String> courseIds);

    Optional<ModuleRecord> findModuleRecordByModuleIdAndCourseRecordIdentityCourseIdAndCourseRecordIdentityUserId(String moduleId, String courseId, String userId);
}
