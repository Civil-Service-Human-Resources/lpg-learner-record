package uk.gov.cslearning.record.dto.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookingDtoFactory {
    private final String learningCatalogueBaseUrl;
    private final String csrsBaseUrl;
    private final IdentityService identityService;

    public BookingDtoFactory(
            @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl,
            @Value("${registry.serviceUrl}") String csrsBaseUrl,
            IdentityService identityService) {
        this.identityService = identityService;
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;
        this.csrsBaseUrl = csrsBaseUrl;
    }

    public Iterable<BookingDto> createBulk(Collection<Booking> bookings) {
        List<String> userUids = bookings.stream().map(b -> b.getLearner().getUid()).collect(Collectors.toList());
        Map<String, Identity> identitiesFromUids = identityService.fetchByUids(userUids);
        List<BookingDto> bookingResults = new ArrayList<>();
        bookings.forEach(b -> {
            String learnerUid = b.getLearner().getUid();
            Identity bookingIdentity = identitiesFromUids.get(learnerUid);
            if (bookingIdentity != null) {
                BookingDto finalBooking = create(b, bookingIdentity.getUsername());
                bookingResults.add(finalBooking);
            } else {
                log.warn(String.format("User uid %s is booked on event %s, but does not exist in the identity database.", learnerUid, b.getEvent().getUid()));
            }
        });
        return bookingResults;
    }

    public BookingDto create(Booking booking) {
        String learnerEmail = identityService.getEmailAddress(booking.getLearner().getUid());
        return create(booking, learnerEmail);
    }

    public BookingDto create(Booking booking, String userEmail) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEvent(UriBuilder.fromUri(learningCatalogueBaseUrl).path(booking.getEvent().getPath()).build());
        bookingDto.setLearner(booking.getLearner().getUid());
        bookingDto.setLearnerEmail(userEmail);
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setConfirmationTime(booking.getConfirmationTime());
        bookingDto.setCancellationTime(booking.getCancellationTime());
        bookingDto.setPoNumber(booking.getPoNumber());
        bookingDto.setBookingReference(booking.getBookingReference());

        if (null != booking.getPaymentDetails()) {
            bookingDto.setPaymentDetails(UriBuilder.fromUri(csrsBaseUrl).path(booking.getPaymentDetails()).build());
        }
        if (booking.getCancellationReason() != null) {
            bookingDto.setCancellationReason(booking.getCancellationReason());
        }

        bookingDto.setStatus(booking.getStatus());
        bookingDto.setAccessibilityOptions(booking.getAccessibilityOptions());

        return bookingDto;
    }
}
