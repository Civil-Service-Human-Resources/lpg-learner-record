package uk.gov.cslearning.record.notifications.dto;

import lombok.Getter;

@Getter
public enum NotificationTemplate {
    REQUIRED_LEARNING_DUE("requiredLearningDue"),
    INVITE_LEARNER("inviteLearner"),
    CANCEL_BOOKING("cancelBooking"),
    CANCEL_EVENT("cancelEvent"),
    BOOKING_CONFIRMED("bookingConfirmed"),
    BOOKING_CONFIRMED_LINE_MANAGER("bookingConfirmedLineManager"),
    BOOKING_REQUEST_LINE_MANAGER("bookingRequestLineManager"),
    BOOKING_CANCELLED_LINE_MANAGER("bookingCancelledLineManager"),
    BOOKING_REQUESTED("bookingRequested"),
    ;

    private final String configName;

    NotificationTemplate(String configName) {
        this.configName = configName;
    }
}
