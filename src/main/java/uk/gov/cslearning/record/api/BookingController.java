package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.BookingService;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/event/{eventId}/booking/{bookingId}", produces = "application/json")
    public ResponseEntity<BookingDto> getBooking(@PathVariable long bookingId) {
        Optional<BookingDto> booking = bookingService.find(bookingId);

        return booking
                .map(b -> new ResponseEntity<>(b, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @PostMapping(value="/event/{eventId}/booking/")
    public ResponseEntity<BookingDto> createBooking(@PathVariable String eventId, @Valid @RequestBody BookingDto booking, UriComponentsBuilder uriBuilder) {

        BookingDto result = bookingService.save(booking);

        return ResponseEntity.created(
                uriBuilder.path("/event/{eventId}/booking/{bookingId}").build(eventId, result.getId())
        ).build();
    }
}
