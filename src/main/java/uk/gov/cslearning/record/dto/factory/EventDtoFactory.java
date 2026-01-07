package uk.gov.cslearning.record.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.api.input.FindEventParams;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.InviteDto;

import java.util.List;

@Component
public class EventDtoFactory {

    private final BookingDtoFactory bookingDtoFactory;
    private final InviteDtoFactory inviteDtoFactory;

    public EventDtoFactory(BookingDtoFactory bookingDtoFactory, InviteDtoFactory inviteDtoFactory) {
        this.bookingDtoFactory = bookingDtoFactory;
        this.inviteDtoFactory = inviteDtoFactory;
    }

    public EventDto create(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setStatus(event.getStatus());
        eventDto.setUid(event.getUid());
        eventDto.setCancellationReason(event.getCancellationReason());
        return eventDto;
    }

    public EventDto create(Event event, FindEventParams params) {
        EventDto eventDto = create(event);
        if (params.isGetBookings() || params.isGetBookingCount()) {
            List<BookingDto> bookings = event.getBookings()
                    .stream().map(bookingDtoFactory::create)
                    .filter(b -> List.of(BookingStatus.CONFIRMED,
                            BookingStatus.REQUESTED).contains(b.getStatus()))
                    .toList();
            if (params.isGetBookingCount()) {
                Integer count = bookings.size();
                eventDto.setActiveBookingCount(count);
            }
            if (params.isGetBookings()) {
                eventDto.setActiveBookings(bookings);
            }
        }
        if (params.isGetInvites()) {
            List<InviteDto> invites = event.getInvites()
                    .stream().map(inviteDtoFactory::create).toList();
            eventDto.setInvites(invites);
        }
        return eventDto;
    }
}
