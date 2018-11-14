package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Invite;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Integer> {

    @Query("SELECT i FROM Invite i INNER JOIN Event e ON i.event = e WHERE e.uid = ?1")
    Collection<Invite> findByEventId(String eventUid);

    @Query("SELECT i FROM Invite i INNER JOIN Event e ON i.event = e WHERE e.id = ?1 AND i.learnerEmail = ?2")
    Optional<Invite> findByEventIdLearnerEmail(int eventId, String learnerEmail);
}
