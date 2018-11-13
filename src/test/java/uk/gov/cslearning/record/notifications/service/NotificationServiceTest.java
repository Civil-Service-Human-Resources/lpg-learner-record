package uk.gov.cslearning.record.notifications.service;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.notifications.dto.MessageDto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {

    private final String notificationsUrl = "http://localhost:9010/notifications/email/";

    private final RestTemplate restTemplate = mock(RestTemplate.class);

    private final NotificationService notificationService = new NotificationService(restTemplate, notificationsUrl);

    @Test
    public void shouldReturnTrueWhenMailSent() {
        MessageDto messageDto = new MessageDto();
        ResponseEntity response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        when(restTemplate.postForEntity(notificationsUrl, messageDto, Void.class)).thenReturn(response);

        assertTrue(notificationService.send(messageDto));

        verify(restTemplate).postForEntity(notificationsUrl, messageDto, Void.class);
    }

    @Test
    public void shouldReturnFalseWhenMailNotSent() {
        MessageDto messageDto = new MessageDto();
        ResponseEntity response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(notificationsUrl, messageDto, Void.class)).thenReturn(response);

        assertFalse(notificationService.send(messageDto));

        verify(restTemplate).postForEntity(notificationsUrl, messageDto, Void.class);
    }
}