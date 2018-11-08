package uk.gov.cslearning.record.repository;

import org.springframework.context.annotation.Lazy;
import uk.gov.cslearning.record.domain.Booking;

public class BookingRepositoryImpl implements CustomBookingRepository {

    private final EventRepository eventRepository;
    private final LearnerRepository learnerRepository;
    private final BookingRepository bookingRepository;

    public BookingRepositoryImpl(EventRepository eventRepository, LearnerRepository learnerRepository, @Lazy BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.learnerRepository = learnerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public <S extends Booking> S saveBooking(S entity) {
        learnerRepository.findByUid(entity.getLearner().getUid()).ifPresent(entity::setLearner);
        eventRepository.findByCatalogueId(entity.getEvent().getCatalogueId()).ifPresent(entity::setEvent);

        return bookingRepository.save(entity);
    }
}
