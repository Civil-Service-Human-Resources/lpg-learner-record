package uk.gov.cslearning.record.config.jobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.gov.cslearning.record.service.scheduler.LearningNotificationPeriod;

import java.util.List;

@ConfigurationProperties(prefix = "jobs.learning-reminders")
@AllArgsConstructor
@Data
public class LearningRemindersConfig {

    List<LearningNotificationPeriod> reminderPeriods;
    private boolean enabled;
    private String cron;

}
