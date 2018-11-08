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
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.dto.factory.BookingDtoFactory;
import uk.gov.cslearning.record.exception.BookingNotFoundException;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
        int bookingId = 99;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        assertEquals(Optional.of(bookingDto), bookingService.find(bookingId));
    }

    @Test
    public void shouldReturnEmptyOptionalIfBookingNotFound() {
        int bookingId = 99;

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
        when(bookingRepository.saveBooking(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.register(unsavedBookingDto));

        InOrder order = inOrder(xApiService, bookingRepository);

        order.verify(xApiService).register(unsavedBookingDto);
        order.verify(bookingRepository).saveBooking(unsavedBooking);
    }

    @Test
    public void shouldSaveBookingButNotRegisterIfNotConfirmed() {
        BookingDto unsavedBookingDto = new BookingDto();
        unsavedBookingDto.setStatus(BookingStatus.REQUESTED);
        Booking unsavedBooking = new Booking();
        BookingDto savedBookingDto = new BookingDto();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(unsavedBookingDto)).thenReturn(unsavedBooking);
        when(bookingRepository.saveBooking(unsavedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.register(unsavedBookingDto));

        verifyZeroInteractions(xApiService);
        verify(bookingRepository).saveBooking(unsavedBooking);
    }

    @Test
    public void shouldUpdateBookingStatus() {
        int bookingId = 99;
        Booking booking = mock(Booking.class);
        Booking updatedBooking = mock(Booking.class);
        Booking savedBooking = mock(Booking.class);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.REQUESTED);

        BookingDto savedBookingDto = new BookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(bookingDtoFactory.create(booking)).thenReturn(bookingDto);

        BookingStatusDto bookingStatus = new BookingStatusDto(BookingStatus.CONFIRMED);

        when(bookingFactory.create(bookingDto)).thenReturn(updatedBooking);
        when(bookingRepository.saveBooking(updatedBooking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.updateStatus(bookingId, bookingStatus));

        verify(xApiService).register(bookingDto);
    }

    @Test
    public void shouldThrowBookingNotFoundException() {
        int bookingId = 99;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        try {
            bookingService.updateStatus(bookingId, new BookingStatusDto(BookingStatus.CONFIRMED));
            fail("Expected BookingNotFoundException");
        } catch (BookingNotFoundException e) {
            assertEquals("Booking does not exist with id: 99", e.getMessage());
        }
    }

    @Test
    public void shouldUnregisterBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(BookingStatus.CANCELLED);
        BookingDto savedBookingDto = new BookingDto();
        Booking booking = new Booking();
        Booking savedBooking = new Booking();

        when(bookingFactory.create(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(savedBooking);
        when(bookingDtoFactory.create(savedBooking)).thenReturn(savedBookingDto);

        assertEquals(savedBookingDto, bookingService.unregister(bookingDto));

        verify(xApiService).unregister(bookingDto);
        verify(bookingRepository).save(booking);
    }
}