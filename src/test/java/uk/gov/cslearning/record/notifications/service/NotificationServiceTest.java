package uk.gov.cslearning.record.notifications.service;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.service.MessageService;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private final String notificationsUrl = "http://localhost:9010/notifications/email/";

    private final RestTemplate restTemplate = mock(RestTemplate.class);

    private final RequestEntityFactory requestEntityFactory = mock(RequestEntityFactory.class);

    private final NotificationService notificationService = new NotificationService(restTemplate, requestEntityFactory, notificationsUrl);

    @Test
    public void shouldReturnTrueWhenMailSent() {
        MessageDto messageDto = new MessageDto();
        ResponseEntity response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        RequestEntity requestEntity = mock(RequestEntity.class);
        when(requestEntityFactory.createPostRequest(notificationsUrl, messageDto)).thenReturn(requestEntity);

        when(restTemplate.exchange(requestEntity, Void.class)).thenReturn(response);

        assertTrue(notificationService.send(messageDto));

        verify(requestEntityFactory).createPostRequest(notificationsUrl, messageDto);
        verify(restTemplate).exchange(requestEntity, Void.class);
    }

    @Test
    public void shouldReturnFalseWhenMailNotSent() {
        MessageDto messageDto = new MessageDto();
        ResponseEntity response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        RequestEntity requestEntity = mock(RequestEntity.class);
        when(requestEntityFactory.createPostRequest(notificationsUrl, messageDto)).thenReturn(requestEntity);

        when(restTemplate.exchange(requestEntity, Void.class)).thenReturn(response);

        assertFalse(notificationService.send(messageDto));

        verify(requestEntityFactory).createPostRequest(notificationsUrl, messageDto);
        verify(restTemplate).exchange(requestEntity, Void.class);
    }
}