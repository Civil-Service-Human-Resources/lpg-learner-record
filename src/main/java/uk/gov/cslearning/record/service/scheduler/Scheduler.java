package uk.gov.cslearning.record.service.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.config.jobs.LearningRemindersConfig;
import uk.gov.cslearning.record.service.LearnerService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@AllArgsConstructor
@Slf4j
public class Scheduler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LearningRemindersConfig config;
    private LearningJob learningJob;
    private LearnerService learnerService;

    @SchedulerLock(name = "incompletedCoursesJob", lockAtMostFor = "PT4H")
    @Scheduled(cron = "${jobs.learning-reminders.cron}")
    public void learningJob() {
        LockAssert.assertLocked();
        if (config.isEnabled()) {
            log.info("Executing learningJob at {}", dateFormat.format(new Date()));
            learningJob.sendReminderNotificationForIncompleteCourses();
            log.info("learningJob complete at {}", dateFormat.format(new Date()));
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() {
        log.info("Executing deleteRecordsLastUpdatedBefore at {}", dateFormat.format(new Date()));
        learnerService.deleteOldStatements();
        log.info("deleteRecordsLastUpdatedBefore complete at {}", dateFormat.format(new Date()));
    }
}
