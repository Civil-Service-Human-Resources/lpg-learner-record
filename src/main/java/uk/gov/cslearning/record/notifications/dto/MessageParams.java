package uk.gov.cslearning.record.notifications.dto;

import lombok.Data;

@Data
public class MessageParams {

    protected final String recipient;

    public MessageParams(String recipient) {
        this.recipient = recipient;
    }
}
