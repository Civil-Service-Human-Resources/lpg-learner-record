package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.CourseRecord;

import java.util.Collection;

@Repository
public interface CourseRecordRepository extends CrudRepository<CourseRecord, Long> {

    Collection<CourseRecord> findByUserId(String userId);

    Iterable<CourseRecord> findByProfession(String profession);

    Iterable<CourseRecord> findByDepartment(String department);
}
