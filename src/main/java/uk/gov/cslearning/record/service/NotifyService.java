package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.HashMap;

@Service
public class NotifyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);
    private static final String EMAIL_PERMISSION = "email address";
    private static final String NAME_PERMISSION = "email";
    private static final String REQUIRED_LEARNING_PERMISSION = "requiredLearning";
    private static final String PERIOD_PERMISSION = "periodPermission";

    private String govNotifyKey;

    public NotifyService(@Value("${govNotify.key}") String govNotifyKey) {
        this.govNotifyKey = govNotifyKey;
    }

    public boolean isNotifyForIncompleteCoursesSuccessful(String email, String requiredLearning, String templateId, String period) {
        LOGGER.debug("Sending {} notification to {}, with required learning {}", period, email, requiredLearning);
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(EMAIL_PERMISSION, email);
        personalisation.put(NAME_PERMISSION, email);
        personalisation.put(REQUIRED_LEARNING_PERMISSION, requiredLearning);
        personalisation.put(PERIOD_PERMISSION, period);

        try {
            NotificationClient client = new NotificationClient(govNotifyKey);
            SendEmailResponse response = client.sendEmail(templateId, email, personalisation, "");

            LOGGER.info("Reminder notify email sent: {}", response.getBody());
            return true;
        } catch (NotificationClientException e) {
            LOGGER.error("Could not send email to GOV notify: ", e.getMessage());
        }

        return false;
    }
}
