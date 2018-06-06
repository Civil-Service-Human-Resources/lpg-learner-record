package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.CourseRecord;

import java.util.Collection;

@Repository
public interface CourseRecordRepository extends CrudRepository<CourseRecord, Long> {

    @Query("SELECT r FROM CourseRecord r WHERE r.identity.userId = ?1")
    Collection<CourseRecord> findByUserId(String userId);

    Iterable<CourseRecord> findByProfession(String profession);

    Iterable<CourseRecord> findByDepartment(String department);
}
