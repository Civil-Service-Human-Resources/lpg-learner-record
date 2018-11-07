package uk.gov.cslearning.record.notifications.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MessageDto {
    private Map<String, String> personalisation;
    private String recipient;
    private String templateId;
}
