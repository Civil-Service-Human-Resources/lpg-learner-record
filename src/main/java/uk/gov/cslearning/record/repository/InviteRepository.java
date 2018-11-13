package uk.gov.cslearning.record.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Invite;

import java.util.Collection;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Integer> {

    @Query("SELECT i FROM Invite i INNER JOIN Event e ON i.event = e WHERE e.uid = ?1")
    Collection<Invite> findByEventId(String eventUid);
}
