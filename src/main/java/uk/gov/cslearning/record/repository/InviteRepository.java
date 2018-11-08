package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Invite;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Integer> {

}
