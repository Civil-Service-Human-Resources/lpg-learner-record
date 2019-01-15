package uk.gov.cslearning.record.dto.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.dto.BookingDto;

import javax.ws.rs.core.UriBuilder;

@Component
public class BookingDtoFactory {
    private final String learningCatalogueBaseUrl;
    private final String csrsBaseUrl;

    public BookingDtoFactory(
            @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl,
            @Value("${registry.serviceUrl}") String csrsBaseUrl) {
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;
        this.csrsBaseUrl = csrsBaseUrl;
    }

    public BookingDto create(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEvent(UriBuilder.fromUri(learningCatalogueBaseUrl).path(booking.getEvent().getPath()).build());
        bookingDto.setLearner(booking.getLearner().getUid());
        bookingDto.setLearnerEmail(booking.getLearner().getLearnerEmail());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setConfirmationTime(booking.getConfirmationTime());
        bookingDto.setCancellationTime(booking.getCancellationTime());
        bookingDto.setPoNumber(booking.getPoNumber());

        if (null != booking.getPaymentDetails()) {
            bookingDto.setPaymentDetails(UriBuilder.fromUri(csrsBaseUrl).path(booking.getPaymentDetails()).build());
        }
        if(booking.getCancellationReason() != null) {
            bookingDto.setCancellationReason(booking.getCancellationReason());
        }

        bookingDto.setStatus(booking.getStatus());
        bookingDto.setAccessibilityOptions(booking.getAccessibilityOptions());

        return bookingDto;
    }
}
