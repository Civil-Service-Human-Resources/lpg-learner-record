package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CourseRecordRepository extends JpaRepository<CourseRecord, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM CourseRecord r WHERE r.identity.userId = ?1")
    Collection<CourseRecord> findByUserId(String userId);

    @Query("SELECT COUNT(mr) FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId = ?1 and mr.state = 'REGISTERED'")
    Integer countRegisteredForEvent(@Param("eventId") String eventId);

    @Query("SELECT cr FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId is not null")
    List<CourseRecord> listEventRecords();

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseRecord r WHERE r.identity.userId = ?1")
    void deleteAllByUid(String uid);

    @Transactional
    @Modifying
    void deleteAllByLastUpdatedBefore(LocalDateTime dateTime);

    @Query("SELECT cr " +
            "FROM CourseRecord cr " +
            "WHERE cr.lastUpdated BETWEEN ?1 AND ?2 ORDER BY cr.identity.userId")
    List<CourseRecord> findAllByLastUpdatedBetween(LocalDateTime from, LocalDateTime to);
}
