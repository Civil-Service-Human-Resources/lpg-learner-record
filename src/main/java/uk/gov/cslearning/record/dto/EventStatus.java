package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.cslearning.record.exception.UnknownStatusException;

import java.util.Arrays;

public enum EventStatus {
    ACTIVE("Active"), CANCELLED("Cancelled");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventStatus forValue(String value) {
        return Arrays.stream(EventStatus.values())
                .filter(v -> v.getValue().equalsIgnoreCase(value) || v.toString().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new UnknownStatusException(value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
