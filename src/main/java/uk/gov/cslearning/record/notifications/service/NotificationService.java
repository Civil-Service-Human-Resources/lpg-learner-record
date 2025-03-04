package uk.gov.cslearning.record.notifications.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.notification.INotificationClient;
import uk.gov.cslearning.record.config.NotificationTemplates;
import uk.gov.cslearning.record.notifications.dto.IMessageParams;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.util.IUtilService;

import java.util.List;

@Service
public class NotificationService {

    private final IUtilService stringUtils;
    private final INotificationClient notificationClient;
    private final NotificationTemplates notificationTemplates;

    public NotificationService(IUtilService stringUtils, INotificationClient notificationClient,
                               NotificationTemplates notificationTemplates) {
        this.stringUtils = stringUtils;
        this.notificationClient = notificationClient;
        this.notificationTemplates = notificationTemplates;
    }

    public void send(IMessageParams message) {
        String templateName = notificationTemplates.getTemplate(message.getTemplate());
        MessageDto messageDto = new MessageDto(message.getRecipient(), message.getPersonalisation(), stringUtils.generateUUID());
        notificationClient.sendEmail(templateName, messageDto);
    }

    public void send(List<IMessageParams> messages) {
        messages.forEach(this::send);
    }
}
