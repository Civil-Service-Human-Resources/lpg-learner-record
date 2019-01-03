package uk.gov.cslearning.record.notifications.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.service.RequestEntityFactory;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;

    private final RequestEntityFactory requestEntityFactory;

    private final String emailNotificationUrl;

    public NotificationService(RestTemplate restTemplate, RequestEntityFactory requestEntityFactory,
                               @Value("${notifications.email}") String emailNotificationUrl) {
        this.restTemplate = restTemplate;
        this.requestEntityFactory = requestEntityFactory;
        this.emailNotificationUrl = emailNotificationUrl;
    }

    public boolean send(MessageDto message) {
        RequestEntity requestEntity = requestEntityFactory.createPostRequest(emailNotificationUrl, message);

        ResponseEntity<Void> response = restTemplate.exchange(requestEntity, Void.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
