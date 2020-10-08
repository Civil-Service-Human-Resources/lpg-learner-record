package uk.gov.cslearning.record.service.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import uk.gov.cslearning.record.service.LearnerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Value("${notifications.lr-refresh-job-enabled}")
    private Boolean refreshJobEnabled;

    @Value("${notifications.completed-job-enabled}")
    private Boolean completedJobEnabled;

    @Value("${notifications.incomplete-job-cron-enabled}")
    private Boolean incompletedJobEnabled;

    @Autowired
    private LearningJob learningJob;

    @Autowired
    private LearnerService learnerService;

    @SchedulerLock(name = "learnerRecordRefresh", lockAtMostFor = "PT4H")
    @Scheduled(cron = "${notifications.lr-refresh-job-cron}")
    public void courseDataRefresh() {
        LockAssert.assertLocked();
        if (refreshJobEnabled) {
            LOGGER.info("Learner Record Refresh at {}", dateFormat.format(new Date()));
            learningJob.learnerRecordRefresh();
            LOGGER.info("Learner Record Refresh complete at {}", dateFormat.format(new Date()));
        }
    }

    @SchedulerLock(name = "completedCoursesJob", lockAtMostFor = "PT4H")
    @Scheduled(cron = "${notifications.completed-job-cron}")
    public void sendNotificationForCompletedLearning() {
        LockAssert.assertLocked();
        if (completedJobEnabled) {
            LOGGER.info("Executing sendLineManagerNotificationForCompletedLearning at {}", dateFormat.format(new Date()));
            learningJob.sendLineManagerNotificationForCompletedLearning();
            LOGGER.info("sendLineManagerNotificationForCompletedLearning complete at {}", dateFormat.format(new Date()));
        }
    }

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
        LOGGER.info("Executing deleteOldRecords at {}", dateFormat.format(new Date()));
        learnerService.deleteOldStatements();
        LOGGER.info("deleteOldRecords complete at {}", dateFormat.format(new Date()));
    }
}
