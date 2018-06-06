package uk.gov.cslearning.record.service.scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private LearningJob learningJob;

    // cron to run every day at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void learningJob() throws Exception{
        LOGGER.info("Executing learningJob at {}", dateFormat.format(new Date()));

        learningJob.sendNotificationForIncompleteCourses();

        LOGGER.info("learningJob complete at {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void sendNotificationForCompletedLearning() throws Exception{
        LOGGER.info("Executing  sendNotificationForCompletedLearning at {}", dateFormat.format(new Date()));

        learningJob.sendNotificationForCompletedLearning();

        LOGGER.info("sendNotificationForCompletedLearning complete at {}", dateFormat.format(new Date()));
    }

}
