package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Invite;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Integer> {

    Collection<Invite> findAllByEventUid(String eventUid);

    Optional<Invite> findByEventUidAndLearnerEmail(String eventUid, String learnerEmail);

    void deleteAllByLearnerEmail(String leanerEmail);
}
