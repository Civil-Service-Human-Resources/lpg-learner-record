package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRecordRepository extends JpaRepository<CourseRecord, Long> {

    @Query("SELECT r FROM CourseRecord r WHERE r.identity.userId = ?1")
    List<CourseRecord> findByUserId(String userId);

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.lastUpdated >= ?1 AND cr.state = 'COMPLETED' AND cr.isRequired = true")
    List<CourseRecord> findCompletedByLastUpdated(LocalDateTime since);

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.identity.courseId = ?1 AND cr.identity.userId = ?2 AND cr.state = 'COMPLETED' AND cr.isRequired = true")
    Optional<CourseRecord> findCompletedByCourseIdAndUserId(String courseId, String userId);

    @Query("SELECT COUNT(mr) FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId = ?1 and mr.state = 'REGISTERED'")
    Integer countRegisteredForEvent(@Param("eventId") String eventId);

    @Query("SELECT cr FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId is not null")
    List<CourseRecord> listEventRecords();

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.identity.userId = ?1 AND cr.identity.courseId = ?2")
    Optional<CourseRecord> getCourseRecord(String userId, String courseId);

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.identity.userId = ?1 AND cr.identity.courseId in (?2)")
    List<CourseRecord> findByUserIdAndCourseIdIn(String userId, List<String> courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseRecord r WHERE r.identity.userId = ?1")
    void deleteAllByUid(String uid);

    @Transactional
    @Modifying
    void deleteAllByLastUpdatedBefore(LocalDateTime dateTime);
}
