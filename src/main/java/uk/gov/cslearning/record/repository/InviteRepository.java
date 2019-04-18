package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.Invite;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Integer> {

    Collection<Invite> findAllByEventUid(String eventUid);

    Optional<Invite> findByEventUidAndLearnerEmail(String eventUid, String learnerEmail);

    @Transactional
    @Modifying
    void deleteAllByLearnerEmail(String leanerEmail);
}
