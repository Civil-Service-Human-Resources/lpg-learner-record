package uk.gov.cslearning.record.service.scheduler;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.LearnerService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Value("${notifications.incompleted-job-enabled}")
    private Boolean incompletedJobEnabled;

    @Autowired
    private LearningJob learningJob;

    @Autowired
    private LearnerService learnerService;

    @SchedulerLock(name = "incompletedCoursesJob", lockAtMostFor = "PT4H")
    @Scheduled(cron = "${notifications.incomplete-job-cron}")
    public void learningJob() {
        LockAssert.assertLocked();
        if (incompletedJobEnabled) {
            LOGGER.info("Executing learningJob at {}", dateFormat.format(new Date()));
            learningJob.sendReminderNotificationForIncompleteCourses();
            LOGGER.info("learningJob complete at {}", dateFormat.format(new Date()));
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() {
        LOGGER.info("Executing deleteRecordsLastUpdatedBefore at {}", dateFormat.format(new Date()));
        learnerService.deleteOldStatements();
        LOGGER.info("deleteRecordsLastUpdatedBefore complete at {}", dateFormat.format(new Date()));
    }
}
