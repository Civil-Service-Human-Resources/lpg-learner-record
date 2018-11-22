package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Context;
import org.junit.Test;
import uk.gov.cslearning.record.dto.BookingDto;

import java.net.URI;

import static org.junit.Assert.*;

public class ContextFactoryTest {

    private ContextFactory contextFactory = new ContextFactory();

    @Test
    public void shouldReturnBookingContextWithCourseAndModuleIds() {
        URI eventUri = URI.create("http://localhost:9001/courses/qVN6_CbwSyuiogLAvHkspw/modules/sVvlS5VYSkGl7hcMxOYguQ/events/BifazkPxRSueda2Xyp5Tag");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setEvent(eventUri);

        Context context = contextFactory.createBookingContext(bookingDto);

        assertEquals("http://cslearning.gov.uk/courses/qVN6_CbwSyuiogLAvHkspw", context.getContextActivities().getParent().get(0).getId());
        assertEquals("http://cslearning.gov.uk/modules/sVvlS5VYSkGl7hcMxOYguQ", context.getContextActivities().getParent().get(1).getId());
    }

    @Test
    public void shouldThrowIllegalStateException() {
        URI eventUri = URI.create("http://localhost:9001/");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setEvent(eventUri);

        try {
            contextFactory.createBookingContext(bookingDto);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Unable to parse event URI", e.getMessage());
        }
    }
}