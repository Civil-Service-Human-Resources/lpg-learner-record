package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.repository.LearnerRepository;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.Instant;

@Component
public class BookingFactory {

    private final IUtilService utilService;
    private final LearnerRepository learnerRepository;

    public BookingFactory(IUtilService utilService, LearnerRepository learnerRepository) {
        this.utilService = utilService;
        this.learnerRepository = learnerRepository;
    }

    public Booking create(BookingDto bookingDto) {
        Learner learner = learnerRepository.findByUid(bookingDto.getLearner())
                .orElse(new Learner(bookingDto.getLearner(), bookingDto.getLearnerEmail()));
        Instant creationTime = utilService.getNowInstant();
        Booking booking = new Booking();
        
        booking.setStatus(bookingDto.getStatus());
        booking.setBookingTime(creationTime);
        if (bookingDto.getStatus().equals(BookingStatus.CONFIRMED)) {
            booking.setConfirmationTime(creationTime);
        }
        if (null != bookingDto.getPaymentDetails()) {
            booking.setPaymentDetails(bookingDto.getPaymentDetails().getPath());
        }
        booking.setLearner(learner);
        booking.setPoNumber(bookingDto.getPoNumber());
        booking.setAccessibilityOptions(bookingDto.getAccessibilityOptions());
        booking.setBookingReference(utilService.generateSaltedString(5));
        return booking;
    }
}
