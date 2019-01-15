package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.service.BookingService;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/event/{eventId}/booking")
    public ResponseEntity<Iterable<BookingDto>> listEventBookings(@PathVariable String eventId) {
        Iterable<BookingDto> result = bookingService.listByEventUid(eventId);

        return new ResponseEntity<>(result, OK);
    }

    @GetMapping(value = "/event/{eventId}/booking/{bookingId}", produces = "application/json")
    public ResponseEntity<BookingDto> getBooking(@PathVariable int bookingId) {
        Optional<BookingDto> booking = bookingService.find(bookingId);

        return booking
                .map(b -> new ResponseEntity<>(b, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping(value = "/event/{eventUid}/learner/{learnerUid}", produces = "application/json")
    public ResponseEntity<BookingDto> getBooking(@PathVariable String eventUid, @PathVariable String learnerUid) {
        Optional<BookingDto> booking = bookingService.find(eventUid, learnerUid);

        return booking
                .map(b -> new ResponseEntity<>(b, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }


    @PostMapping(value="/event/{eventId}/booking/")
    public ResponseEntity<BookingDto> createBooking(@PathVariable String eventId, @Valid @RequestBody BookingDto booking, UriComponentsBuilder uriBuilder) {
        BookingDto result = bookingService.register(booking);

        return ResponseEntity.created(
                uriBuilder.path("/event/{eventId}/booking/{bookingId}").build(eventId, result.getId())
        ).build();
    }

    @PatchMapping(value = "/event/{eventUid}/learner/{learnerUid}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable String eventUid, @PathVariable String learnerUid, @Valid @RequestBody BookingStatusDto bookingStatus) {
        BookingDto result = bookingService.updateStatus(eventUid, learnerUid, bookingStatus);

        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/event/{eventId}/booking/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable String eventId, @PathVariable int bookingId, @Valid @RequestBody BookingStatusDto bookingStatus) {
        BookingDto result = bookingService.updateStatus(bookingId, bookingStatus);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/event/booking/cancellationReasons")
    public ResponseEntity<Map<String, String>> getCancellationReasons() {
        return new ResponseEntity<>(BookingCancellationReason.getManagementKeyValuePairs(), OK);
    }

    @GetMapping(value ="/event/booking/userCancellationReasons")
    public ResponseEntity<Map<String, String>> getUserCancellationReasons() {
        return new ResponseEntity<>(BookingCancellationReason.getUserKeyValuePairs(), OK);
    }
}