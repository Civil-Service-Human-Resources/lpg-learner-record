package uk.gov.cslearning.record.notifications.dto;

import uk.gov.cslearning.record.domain.Booking;

import java.util.Map;

public class RequestBookingMessageParams extends MessageParams implements IMessageParams {
    private final CourseMessageDetails courseMessageDetails;
    private final String accessibility;
    private final String bookingReference;

    public RequestBookingMessageParams(String recipient, CourseMessageDetails courseMessageDetails,
                                       String accessibility, String bookingReference) {
        super(recipient);
        this.courseMessageDetails = courseMessageDetails;
        this.accessibility = accessibility;
        this.bookingReference = bookingReference;
    }

    public static RequestBookingMessageParams createFromBooking(Booking booking, CourseMessageDetails courseMessageDetails) {
        return new RequestBookingMessageParams(booking.getLearner().getLearnerEmail(), courseMessageDetails,
                booking.getAccessibilityOptionsString(), booking.getBookingReference());
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.BOOKING_REQUESTED;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "accessibility", accessibility,
                "bookingReference", bookingReference
        );
    }
}
