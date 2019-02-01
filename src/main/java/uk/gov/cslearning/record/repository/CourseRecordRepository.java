package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;

import javax.persistence.LockModeType;
import java.util.Collection;

@Repository
public interface CourseRecordRepository extends CrudRepository<CourseRecord, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM CourseRecord r WHERE r.identity.userId = ?1")
    Collection<CourseRecord> findByUserId(String userId);

    Iterable<CourseRecord> findByProfession(String profession);

    Iterable<CourseRecord> findByDepartment(String department);

    @Query("SELECT COUNT(mr) FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId = ?1 and mr.state = 'REGISTERED'")
    Integer countRegisteredForEvent(@Param("eventId") String eventId);

    @Query("SELECT cr FROM CourseRecord cr JOIN cr.moduleRecords mr where mr.eventId is not null")
    Iterable<CourseRecord> listEventRecords();

    @Transactional
    @Modifying
    @Query("DELETE FROM CourseRecord r WHERE r.identity.userId = ?1")
    void deleteAllByUid(String uid);
}
