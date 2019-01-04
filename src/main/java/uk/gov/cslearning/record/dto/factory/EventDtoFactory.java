package uk.gov.cslearning.record.dto.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import javax.ws.rs.core.UriBuilder;

@Component
public class EventDtoFactory {
    private final String learningCatalogueBaseUrl;

    public EventDtoFactory(
            @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl) {
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;
    }

    public EventDto create(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setStatus(EventStatus.forValue(event.getStatus()));
        eventDto.setUri(UriBuilder.fromUri(learningCatalogueBaseUrl).path(event.getPath()).build());
        eventDto.setUid(event.getUid());
        eventDto.setCancellationReason(event.getCancellationReason());
        return eventDto;
    }
}
