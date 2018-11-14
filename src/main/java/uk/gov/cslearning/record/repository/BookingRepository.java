package uk.gov.cslearning.record.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Booking;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer>, CustomBookingRepository {
}
