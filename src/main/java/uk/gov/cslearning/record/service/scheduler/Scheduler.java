package uk.gov.cslearning.record.service.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.LearnerService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@AllArgsConstructor
@Slf4j
public class Scheduler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LearnerService learnerService;

    @Scheduled(cron = "0 0 4 * * *")
    @SchedulerLock(name = "deleteOldStatementsJob", lockAtMostFor = "PT4H")
    public void deleteOldStatements() {
        log.info("Executing deleteRecordsLastUpdatedBefore at {}", dateFormat.format(new Date()));
        learnerService.deleteOldStatements();
        log.info("deleteRecordsLastUpdatedBefore complete at {}", dateFormat.format(new Date()));
    }
}
