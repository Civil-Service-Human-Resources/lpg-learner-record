package uk.gov.cslearning.record.notifications.dto;

import uk.gov.cslearning.record.domain.Booking;

import java.util.Map;

public class CancelBookingMessageParams extends MessageParams implements IMessageParams {

    private final CourseMessageDetails courseMessageDetails;
    private final String cancellationReason;
    private final String bookingReference;

    public CancelBookingMessageParams(String recipient, CourseMessageDetails courseMessageDetails, String cancellationReason, String bookingReference) {
        super(recipient);
        this.courseMessageDetails = courseMessageDetails;
        this.cancellationReason = cancellationReason;
        this.bookingReference = bookingReference;
    }

    public static CancelBookingMessageParams createFromBooking(Booking booking, CourseMessageDetails courseMessageDetails) {
        return new CancelBookingMessageParams(booking.getLearner().getLearnerEmail(), courseMessageDetails,
                booking.getCancellationReason().getValue(), booking.getBookingReference());
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.CANCEL_BOOKING;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "learnerName", recipient,
                "cancellationReason", cancellationReason,
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "bookingReference", bookingReference
        );
    }
}
