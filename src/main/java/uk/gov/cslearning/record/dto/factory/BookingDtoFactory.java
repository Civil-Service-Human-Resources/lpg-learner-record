package uk.gov.cslearning.record.dto.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;

import java.net.URI;
import java.util.Collection;

@Slf4j
@Component
public class BookingDtoFactory {
    private final String learningCatalogueBaseUrl;
    private final String csrsBaseUrl;

    public BookingDtoFactory(
            @Value("${catalogue.serviceUrl}") String learningCatalogueBaseUrl,
            @Value("${registry-service.serviceUrl}") String csrsBaseUrl) {
        this.learningCatalogueBaseUrl = learningCatalogueBaseUrl;
        this.csrsBaseUrl = csrsBaseUrl;
    }

    public Iterable<BookingDto> createBulk(Collection<Booking> bookings) {
        return bookings.stream().map(this::create).toList();
    }

    public BookingDto create(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEvent(URI.create(String.format("%s%s", learningCatalogueBaseUrl, booking.getEvent().getPath())));
        Learner learner = booking.getLearner();
        bookingDto.setLearner(learner.getUid());
        bookingDto.setLearnerEmail(learner.getLearnerEmail());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setConfirmationTime(booking.getConfirmationTime());
        bookingDto.setCancellationTime(booking.getCancellationTime());
        bookingDto.setPoNumber(booking.getPoNumber());
        bookingDto.setBookingReference(booking.getBookingReference());

        if (null != booking.getPaymentDetails()) {
            bookingDto.setPaymentDetails(URI.create(String.format("%s%s", csrsBaseUrl, booking.getPaymentDetails())));
        }
        if (booking.getCancellationReason() != null) {
            bookingDto.setCancellationReason(booking.getCancellationReason());
        }

        bookingDto.setStatus(booking.getStatus());
        bookingDto.setAccessibilityOptions(booking.getAccessibilityOptions());

        return bookingDto;
    }
}
