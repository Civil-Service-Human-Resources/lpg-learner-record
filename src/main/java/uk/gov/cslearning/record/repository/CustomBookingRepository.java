package uk.gov.cslearning.record.repository;

import uk.gov.cslearning.record.domain.Booking;

public interface CustomBookingRepository {
    <S extends Booking> S saveBooking(S entity);
}
