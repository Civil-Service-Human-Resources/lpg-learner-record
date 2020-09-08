package uk.gov.cslearning.record.service.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.LearnerService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private LearningJob learningJob;

    @Autowired
    private LearnerService learnerService;

    @Async
    public void courseDataRefresh() {
        LOGGER.info("Learner Record Refresh at {}", dateFormat.format(new Date()));
        learningJob.learnerRecordRefresh();
        LOGGER.info("Learner Record Refresh complete at {}", dateFormat.format(new Date()));
    }

    @Async
    public void learningJob() {
        LOGGER.info("Executing learningJob at {}", dateFormat.format(new Date()));
        //learningJob.sendReminderNotificationForIncompleteCourses();
        LOGGER.info("Skipping sendReminderNotificationForIncompleteCourses at {}", dateFormat.format(new Date()));
        LOGGER.info("learningJob complete at {}", dateFormat.format(new Date()));
    }

    @Async
    public void sendNotificationForCompletedLearning() {
        LOGGER.info("Executing sendLineManagerNotificationForCompletedLearning at {}", dateFormat.format(new Date()));
        learningJob.sendLineManagerNotificationForCompletedLearning();
        LOGGER.info("sendLineManagerNotificationForCompletedLearning complete at {}", dateFormat.format(new Date()));
    }

    public void deleteOldStatements() {
        LOGGER.info("Executing deleteOldRecords at {}", dateFormat.format(new Date()));
        learnerService.deleteOldStatements();
        LOGGER.info("deleteOldRecords complete at {}", dateFormat.format(new Date()));
    }
}
