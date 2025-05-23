package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT cr FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId is not null")
    List<CourseRecord> listEventRecords();

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.identity.userId = ?1 AND cr.identity.courseId = ?2")
    Optional<CourseRecord> getCourseRecord(String userId, String courseId);

    @Query("SELECT cr FROM CourseRecord cr WHERE cr.identity.userId in (?1) AND (?2 is NULL or cr.identity.courseId in (?2))")
    List<CourseRecord> findByUserIdAndCourseIdIn(List<String> userIds, List<String> courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseRecord r WHERE r.identity.userId = ?1")
    void deleteAllByUid(String uid);

    @Transactional
    @Modifying
    void deleteAllByLastUpdatedBefore(LocalDateTime dateTime);
}
