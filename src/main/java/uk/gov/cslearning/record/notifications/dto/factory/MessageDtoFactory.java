package uk.gov.cslearning.record.notifications.dto.factory;

import uk.gov.cslearning.record.notifications.dto.MessageDto;

import java.util.Map;

public class MessageDtoFactory {
    public MessageDto create(String recipient, String templateId, Map<String, String> personalisation) {
        MessageDto messageDto = new MessageDto();
        messageDto.setRecipient(recipient);
        messageDto.setTemplateId(templateId);
        messageDto.setPersonalisation(personalisation);

        return messageDto;
    }
}
