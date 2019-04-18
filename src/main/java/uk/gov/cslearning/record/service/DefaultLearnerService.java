package uk.gov.cslearning.record.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.LearnerRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class DefaultLearnerService implements LearnerService {

    private final LearnerRepository learnerRepository;

    private final BookingService bookingService;

    private final UserRecordService userRecordService;

    private final InviteService inviteService;

    private final NotificationService notificationService;

    private final int dataRetentionTime;

    public DefaultLearnerService(@Value("${retention.timeInMonths}") int dataRetentionTime, LearnerRepository learnerRepository, BookingService bookingService, UserRecordService userRecordService, InviteService inviteService, NotificationService notificationService) {
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
        DateTime dateTime = DateTime.now().minusMonths(dataRetentionTime);
        Instant instant = DateTimeFormatter.ISO_DATE_TIME.parse(dateTime.toString(), Instant::from);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        bookingService.deleteAllByAge(instant);
        notificationService.deleteAllByAge(localDateTime);
        userRecordService.deleteOldRecords(dateTime, localDateTime);
    }
}
