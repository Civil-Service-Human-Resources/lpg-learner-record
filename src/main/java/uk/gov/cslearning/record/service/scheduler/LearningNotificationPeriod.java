package uk.gov.cslearning.record.service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LearningNotificationPeriod {

    private final String text;
    private final Long days;

}
