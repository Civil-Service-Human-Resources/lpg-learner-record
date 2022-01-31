package uk.gov.cslearning.record.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.BookingService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ReportController {

    private final BookingService bookingService;

    public ReportController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/reporting/bookings", params = {"from", "to"})
    public ResponseEntity<Iterable<BookingDto>> listBookings(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Iterable<BookingDto> bookings = bookingService.findAllForPeriod(from, to);

        return new ResponseEntity<>(bookings, OK);
    }
}
