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
    private static final String EMAIL_PERMISSION = "email";
    private static final String REQUIRED_LEARNING_PERMISSION = "requiredLearning";

    @Value("${govNotify.key}")
    private String govNotifyKey;

    public void notify(String email, String requiredLearning, String templateId) throws NotificationClientException {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(EMAIL_PERMISSION, email);
        personalisation.put(REQUIRED_LEARNING_PERMISSION, requiredLearning);

        NotificationClient client = new NotificationClient(govNotifyKey);
        SendEmailResponse response = client.sendEmail(templateId, email, personalisation, "");

        LOGGER.info("Notify email sent: {}", response.getBody());
    }

}
