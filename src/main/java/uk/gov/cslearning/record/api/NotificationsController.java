package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.service.scheduler.LearningJob;
import uk.gov.service.notify.NotificationClientException;

@RestController
@RequestMapping("/notifications")
@Profile("test")
public class NotificationsController {

    /*
     * Matt - This class will only be available if the 'test' profile is added to the application config/Terraform.
     * By hitting this URL, we will manually kick of the notifications job which is currently on the scheduler.
     * Currently just used for development/testing purposes,
     * but we may choose to extend this class out with more functionality at a later date.
     * */
    @Autowired
    private LearningJob learningJob;

    @GetMapping
    public ResponseEntity runNotifications() throws NotificationClientException {
        learningJob.sendNotificationForIncompleteCourses();
        return new ResponseEntity<>("Notifications job manually executed.", HttpStatus.OK);
    }
}