package uk.gov.cslearning.record.notifications.dto;

import lombok.Getter;

@Getter
public enum NotificationTemplate {
    REQUIRED_LEARNING_DUE("requiredLearningDue"),
    INVITE_LEARNER("inviteLearner"),
    CANCEL_BOOKING("cancelBooking"),
    BOOKING_CONFIRMED("bookingConfirmed"),
    BOOKING_CONFIRMED_LINE_MANAGER("bookingConfirmedLineManager"),
    BOOKING_CANCELLED_LINE_MANAGER("bookingCancelledLineManager"),
    ;

    private final String configName;

    NotificationTemplate(String configName) {
        this.configName = configName;
    }
}
