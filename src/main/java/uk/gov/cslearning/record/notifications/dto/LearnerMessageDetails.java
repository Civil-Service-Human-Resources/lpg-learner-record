package uk.gov.cslearning.record.notifications.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class LearnerMessageDetails {
    private final String learnerName;
    private final String learnerEmail;
}
