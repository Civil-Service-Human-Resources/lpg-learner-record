package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public class ConfirmBookingLMMessageParams extends MessageParams implements IMessageParams {

    private final LearnerMessageDetails learnerMessageDetails;
    private final CourseMessageDetails courseMessageDetails;
    private final String bookingReference;

    public ConfirmBookingLMMessageParams(String recipient, LearnerMessageDetails learnerMessageDetails, CourseMessageDetails courseMessageDetails, String bookingReference) {
        super(recipient);
        this.learnerMessageDetails = learnerMessageDetails;
        this.courseMessageDetails = courseMessageDetails;
        this.bookingReference = bookingReference;
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.BOOKING_CONFIRMED_LINE_MANAGER;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", learnerMessageDetails.getLearnerName(),
                "learnerEmail", learnerMessageDetails.getLearnerEmail(),
                "recipient", recipient,
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "cost", courseMessageDetails.getCostInPounds(),
                "bookingReference", bookingReference
        );
    }
}
