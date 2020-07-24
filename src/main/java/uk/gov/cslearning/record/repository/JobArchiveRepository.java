package uk.gov.cslearning.record.repository;

import java.util.Optional;

import uk.gov.cslearning.record.domain.JobArchive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobArchiveRepository extends JpaRepository<JobArchive, Long> {
    @Query("SELECT ja FROM JobArchive ja WHERE name = ?1 AND ja.lastRun IN (SELECT MAX(ja.lastRun) FROM ja WHERE name = ?1)")
    Optional<JobArchive> findLatestByName(String jobName);
}
