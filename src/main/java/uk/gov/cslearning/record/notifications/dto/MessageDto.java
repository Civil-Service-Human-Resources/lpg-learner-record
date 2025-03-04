package uk.gov.cslearning.record.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MessageDto {

    private final String recipient;
    private final Map<String, String> personalisation;
    private final String reference;
}
