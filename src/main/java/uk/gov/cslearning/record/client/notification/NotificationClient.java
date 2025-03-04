package uk.gov.cslearning.record.client.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.notifications.dto.MessageDto;

@Slf4j
@Component
public class NotificationClient implements INotificationClient {
    private final IHttpClient httpClient;

    @Value("${notify.sendEmailEndpointTemplate}")
    private String sendEmailEndpointTemplate;

    public NotificationClient(@Qualifier("notificationHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void sendEmail(String templateName, MessageDto messageDto) {
        log.debug(String.format("Sending %s email to %s", templateName, messageDto.getRecipient()));
        String emailEndpoint = sendEmailEndpointTemplate.replace("{{TEMPLATE_NAME}}", templateName);
        RequestEntity<MessageDto> request = RequestEntity
                .post(emailEndpoint)
                .body(messageDto);
        httpClient.executeRequest(request, Void.class);
        log.debug(String.format("Email sent to %s", messageDto.getRecipient()));
    }
}
