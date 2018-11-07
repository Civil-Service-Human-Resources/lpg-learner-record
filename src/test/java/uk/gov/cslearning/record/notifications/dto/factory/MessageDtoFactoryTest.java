package uk.gov.cslearning.record.notifications.dto.factory;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.cslearning.record.notifications.dto.MessageDto;

import java.util.Map;

import static org.junit.Assert.*;

public class MessageDtoFactoryTest {

    private final MessageDtoFactory messageDtoFactory = new MessageDtoFactory();

    @Test
    public void shouldReturnMessageDto() {
        String recipient = "user@example.org";
        String templateId = "template-id";
        Map<String, String> personalisation = ImmutableMap.of("name", "test-name");

        MessageDto messageDto = messageDtoFactory.create(recipient, templateId, personalisation);

        assertEquals(recipient, messageDto.getRecipient());
        assertEquals(templateId, messageDto.getTemplateId());
        assertEquals(personalisation, messageDto.getPersonalisation());
    }
}