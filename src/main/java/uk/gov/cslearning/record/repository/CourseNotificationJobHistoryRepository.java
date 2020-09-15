package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.CourseNotificationJobHistory;

import java.util.Optional;

@Repository
public interface CourseNotificationJobHistoryRepository extends JpaRepository<CourseNotificationJobHistory, Integer> {
    @Query("SELECT c1 from CourseNotificationJobHistory c1 " +
            "WHERE c1.name = 'COMPLETED_COURSES_JOB' " +
            "AND c1.completedAt IN (SELECT MAX(c2.completedAt) FROM CourseNotificationJobHistory c2)")
    Optional<CourseNotificationJobHistory> findLastCompletedCoursesJobRecord();

    @Query("SELECT c1 from CourseNotificationJobHistory c1 " +
            "WHERE c1.name = 'LEARNER_RECORD_REFRESH' " +
            "AND c1.completedAt IN (SELECT MAX(c2.completedAt) FROM CourseNotificationJobHistory c2)")
    Optional<CourseNotificationJobHistory> findLastLearnerRecordRefreshRecord();

}
