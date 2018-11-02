package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.factory.BookingFactory;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatus;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBookingServiceTest {

    @Mock
    private BookingFactory bookingFactory;

    @Mock
    private BookingDtoFactory bookingDtoFactory;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private XApiService xApiService;

    @InjectMocks
    private DefaultBookingService bookingService;

    @Test
    public void shouldFindBookingById() {
        long bookingId = 99L;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        assertEquals(Optional.of(bookingDto), bookingService.find(bookingId));
    }

    @Test
    public void shouldReturnEmptyOptionalIfBookingNotFound() {
        long bookingId = 99L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), bookingService.find(bookingId));
    }

    @Test
    public void shouldRegisterAndSaveBooking() {
        BookingDto unsavedBookingDto = new BookingDto();
        unsavedBookingDto.setStatus(BookingStatus.CONFIRMED);
        Booking unsavedBooking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(unsavedBookingDto)).thenReturn(unsavedBooking);
        when(bookingRepository.save(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.save(unsavedBookingDto));

        InOrder order = inOrder(xApiService, bookingRepository);

        order.verify(xApiService).register(unsavedBookingDto);
        order.verify(bookingRepository).save(unsavedBooking);
    }

    @Test
    public void saveSaveBookingButNotRegisterIfNotApproved() {
        BookingDto unsavedBookingDto = new BookingDto();
        unsavedBookingDto.setStatus(BookingStatus.REQUESTED);
        Booking unsavedBooking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(unsavedBookingDto)).thenReturn(unsavedBooking);
        when(bookingRepository.save(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.save(unsavedBookingDto));

        verifyZeroInteractions(xApiService);
        verify(bookingRepository).save(unsavedBooking);
    }

}