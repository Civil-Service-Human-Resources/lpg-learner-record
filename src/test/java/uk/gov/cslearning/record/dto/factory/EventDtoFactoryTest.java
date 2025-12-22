package uk.gov.cslearning.record.dto.factory;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventDtoFactoryTest {

    @Mock
    private InviteDtoFactory inviteDtoFactory;

    @Mock
    private BookingDtoFactory bookingDtoFactory;

    @InjectMocks
    private EventDtoFactory eventDtoFactory;

    @Test
    public void shouldReturnEventDto() {
        EventStatus status = EventStatus.ACTIVE;
        String uid = "eventUid-uid";

        Event event = new Event();
        event.setStatus(status);
        event.setUid(uid);
        event.setCancellationReason(CancellationReason.UNAVAILABLE);

        EventDto eventDto = eventDtoFactory.create(event);

        assertEquals(status, eventDto.getStatus());
        assertEquals(uid, eventDto.getUid());
        assertEquals(CancellationReason.UNAVAILABLE, eventDto.getCancellationReason());
    }
}
