package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.BookingService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ReportController {

    private final BookingService bookingService;

    public ReportController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/reporting/bookings")
    public ResponseEntity<List<BookingDto>> listBookings() {
        List<BookingDto> bookings = bookingService.findAll();

        return new ResponseEntity<>(bookings, OK);
    }
}
