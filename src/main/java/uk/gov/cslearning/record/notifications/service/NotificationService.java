package uk.gov.cslearning.record.notifications.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.notifications.dto.MessageDto;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;
    private final String emailNotificationUrl;

    public NotificationService(RestTemplate restTemplate,
                               @Value("${notifications.emailUrl}") String emailNotificationUrl) {
        this.restTemplate = restTemplate;
        this.emailNotificationUrl = emailNotificationUrl;
    }

    public boolean send(MessageDto message) {
        ResponseEntity<Void> response = restTemplate.postForEntity(emailNotificationUrl, message, Void.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
