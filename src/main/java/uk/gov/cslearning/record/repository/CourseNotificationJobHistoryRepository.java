package uk.gov.cslearning.record.repository;

import java.util.Optional;

import uk.gov.cslearning.record.domain.CourseNotificationJobHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseNotificationJobHistoryRepository extends JpaRepository<CourseNotificationJobHistory, Integer> {
    @Query("SELECT c1 from CourseNotificationJobHistory c1 " +
        "WHERE c1.name = 'COMPLETED_COURSES_JOB' " +
        "AND c1.completed_at IN (SELECT MAX(c2.completed_at) FROM CourseNotificationJobHistory c2)")
    Optional<CourseNotificationJobHistory> findLastCompletedCoursesJobRecord();
}
