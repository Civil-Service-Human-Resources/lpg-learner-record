package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public class CancelBookingLMMessageParams extends MessageParams implements IMessageParams {
    private final CourseMessageDetails courseMessageDetails;
    private final LearnerMessageDetails learnerMessageDetails;
    private final String bookingReference;

    public CancelBookingLMMessageParams(String recipient, CourseMessageDetails courseMessageDetails, LearnerMessageDetails learnerMessageDetails, String bookingReference) {
        super(recipient);
        this.courseMessageDetails = courseMessageDetails;
        this.learnerMessageDetails = learnerMessageDetails;
        this.bookingReference = bookingReference;
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.BOOKING_CANCELLED_LINE_MANAGER;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "recipient", recipient,
                "learnerName", learnerMessageDetails.getLearnerName(),
                "learnerEmail", learnerMessageDetails.getLearnerEmail(),
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "cost", courseMessageDetails.getCostInPounds(),
                "bookingReference", bookingReference
        );
    }
}
