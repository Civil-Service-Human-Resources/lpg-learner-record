package uk.gov.cslearning.record.dto;

import lombok.Data;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.State;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LearnerRecordEvent {
    private String bookingReference;
    private String courseId;
    private String courseName;
    private String moduleId;
    private String moduleName;
    private String eventId;
    private BigDecimal cost;
    private LocalDate date;
    private String delegateName;
    private String delegateEmailAddress;
    private String delegateUid;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentMethod;
    private String paymentDetails;
    private State state;
}
