package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.service.scheduler.LearningJob;
import uk.gov.service.notify.NotificationClientException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/notifications")
@Profile("test")
public class TestNotificationsController {

    @Autowired
    private LearningJob learningJob;

    @GetMapping
    public ResponseEntity runNotifications() throws NotificationClientException {
        learningJob.sendNotificationForIncompleteCourses();
        return new ResponseEntity<>(OK);
    }
}
