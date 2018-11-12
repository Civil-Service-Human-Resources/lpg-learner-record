package uk.gov.cslearning.record.dto;

import lombok.Data;

import java.net.URI;

@Data
public class EventDto {
    private String uid;
    private URI uri;
    private EventStatus status;
}
