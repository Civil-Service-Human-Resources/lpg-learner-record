package uk.gov.cslearning.record.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.dto.InviteDto;

import java.util.Collection;

public interface InviteService {
    @Transactional(readOnly = true)
    Collection<InviteDto> findByEventId(String eventId);

    @Transactional(readOnly = true)
    InviteDto findInvite(int inviteId);

    @Transactional
    InviteDto save(InviteDto invite);
}
