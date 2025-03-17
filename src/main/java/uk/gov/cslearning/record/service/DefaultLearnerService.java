package uk.gov.cslearning.record.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.LearnerRepository;
import uk.gov.cslearning.record.util.UtilService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class DefaultLearnerService implements LearnerService {

    private final UtilService utilService;
    private final LearnerRepository learnerRepository;

    private final BookingService bookingService;

    private final UserRecordService userRecordService;

    private final InviteService inviteService;

    private final NotificationService notificationService;

    private final int dataRetentionTime;

    public DefaultLearnerService(UtilService utilService, @Value("${retention.timeInMonths}") int dataRetentionTime, LearnerRepository learnerRepository, BookingService bookingService, UserRecordService userRecordService, InviteService inviteService, NotificationService notificationService) {
        this.utilService = utilService;
        this.learnerRepository = learnerRepository;
        this.bookingService = bookingService;
        this.userRecordService = userRecordService;
        this.inviteService = inviteService;
        this.notificationService = notificationService;
        this.dataRetentionTime = dataRetentionTime;
    }

    public void deleteLearnerByUid(String uid) {
        notificationService.deleteByLearnerUid(uid);
        learnerRepository.findByUid(uid).ifPresent(learner -> {
            bookingService.deleteAllByLearner(learner);
            inviteService.deleteByLearnerEmail(learner.getLearnerEmail());
            learnerRepository.delete(learner);
        });

        userRecordService.deleteUserRecords(uid);
    }

    public void deleteOldStatements() {
        LocalDateTime localDateTime = utilService.getNowDateTime().minus(dataRetentionTime, ChronoUnit.MONTHS);
        log.info("localdatetime {}", localDateTime);
        bookingService.deleteAllByAge(localDateTime.toInstant(ZoneOffset.UTC));
        notificationService.deleteAllByAge(localDateTime);
        userRecordService.deleteRecordsLastUpdatedBefore(localDateTime);
    }
}
