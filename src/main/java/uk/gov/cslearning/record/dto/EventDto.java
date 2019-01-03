package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.net.URI;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private Integer id;
    private String uid;
    private URI uri;
    private EventStatus status;
    private CancellationReason cancellationReason;
}
