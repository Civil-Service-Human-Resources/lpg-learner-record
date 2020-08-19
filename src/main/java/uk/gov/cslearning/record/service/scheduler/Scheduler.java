package uk.gov.cslearning.record.service.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import uk.gov.cslearning.record.service.LearnerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private LearningJob learningJob;

    @Autowired
    private LearnerService learnerService;

    @SchedulerLock(name = "incompletedCoursesJob", lockAtMostFor = "PT4H")
    // cron to run every day at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void learningJob() throws Exception {
        LockAssert.assertLocked();
        LOGGER.info("Executing learningJob at {}", dateFormat.format(new Date()));

        //learningJob.sendReminderNotificationForIncompleteCourses();
        LOGGER.info("Skipping sendReminderNotificationForIncompleteCourses at {}", dateFormat.format(new Date()));

        LOGGER.info("learningJob complete at {}", dateFormat.format(new Date()));
    }

    @SchedulerLock(name = "completedCoursesJob", lockAtMostFor = "PT4H")
    @Scheduled(cron = "0 */3 * * * *")
    public void sendNotificationForCompletedLearning() throws Exception {
        LockAssert.assertLocked();

        LOGGER.info("Executing sendLineManagerNotificationForCompletedLearning at {}", dateFormat.format(new Date()));

        learningJob.sendLineManagerNotificationForCompletedLearning();

        LOGGER.info("sendLineManagerNotificationForCompletedLearning complete at {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldStatements() throws Exception {
        LOGGER.info("Executing deleteOldRecords at {}", dateFormat.format(new Date()));

        learnerService.deleteOldStatements();

        LOGGER.info("deleteOldRecords complete at {}", dateFormat.format(new Date()));
    }
}
