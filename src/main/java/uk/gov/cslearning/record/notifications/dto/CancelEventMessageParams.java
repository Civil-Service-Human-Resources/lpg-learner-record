package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public class CancelEventMessageParams extends MessageParams implements IMessageParams {

    private final CourseMessageDetails courseMessageDetails;
    private final String cancellationReason;
    private final String bookingReference;

    public CancelEventMessageParams(String recipient, CourseMessageDetails courseMessageDetails,
                                    String cancellationReason, String bookingReference) {
        super(recipient);
        this.courseMessageDetails = courseMessageDetails;
        this.cancellationReason = cancellationReason;
        this.bookingReference = bookingReference;
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.CANCEL_EVENT;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "cancellationReason", cancellationReason,
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "bookingReference", bookingReference
        );
    }
}
