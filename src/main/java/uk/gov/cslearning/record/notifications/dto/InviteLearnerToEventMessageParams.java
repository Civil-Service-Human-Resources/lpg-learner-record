package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public class InviteLearnerToEventMessageParams extends MessageParams implements IMessageParams {

    private final CourseMessageDetails courseMessageDetails;
    private final String inviteLink;

    public InviteLearnerToEventMessageParams(String recipient, CourseMessageDetails courseMessageDetails,
                                             String inviteLink) {
        super(recipient);
        this.courseMessageDetails = courseMessageDetails;
        this.inviteLink = inviteLink;
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.INVITE_LEARNER;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "learnerName", recipient,
                "courseTitle", courseMessageDetails.getCourseTitle(),
                "courseDate", courseMessageDetails.getCourseDate(),
                "courseLocation", courseMessageDetails.getCourseLocation(),
                "inviteLink", inviteLink
        );
    }
}
