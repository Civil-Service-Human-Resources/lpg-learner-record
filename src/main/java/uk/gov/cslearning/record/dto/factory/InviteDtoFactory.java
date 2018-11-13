package uk.gov.cslearning.record.dto.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Invite;
import uk.gov.cslearning.record.dto.InviteDto;

import javax.ws.rs.core.UriBuilder;

@Component
public class InviteDtoFactory {
    private final String learningCatalogueBaseUrl;

    public InviteDtoFactory(@Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl){
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;
    }

    public InviteDto create(Invite invite){
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(invite.getId());
        inviteDto.setEvent(UriBuilder.fromUri(learningCatalogueBaseUrl).path(invite.getEvent().getPath()).build());
        inviteDto.setLearnerEmail(invite.getLearnerEmail());

        return inviteDto;
    }
}
