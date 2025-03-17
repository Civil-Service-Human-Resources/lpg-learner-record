package uk.gov.cslearning.record.client.notification;


import uk.gov.cslearning.record.notifications.dto.MessageDto;

public interface INotificationClient {
    void sendEmail(String templateName, MessageDto messageDto);
}
