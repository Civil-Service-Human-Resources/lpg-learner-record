package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.InviteDto;

import java.util.Collection;
import java.util.Optional;

public interface InviteService {
    @Transactional(readOnly = true)
    Collection<InviteDto> findByEventId(String eventId);

    @Transactional(readOnly = true)
    Optional<InviteDto> findInvite(int inviteId);

    @Transactional
    InviteDto save(String eventUid, InviteDto invite);

    @Transactional
    void deleteByLearnerUid(String learnerUid);
}
