package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.cslearning.record.exception.UnknownStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CancellationReason {
    UNAVAILABLE("the eventUid is no longer available"), VENUE("short notice unavailability of the venue");

    private final String value;

    CancellationReason(String value) {
        this.value = value;
    }

    public static Map<String, String> getKeyValuePairs() {
        Map<String, String> map = new HashMap<>();
        map.put("UNAVAILABLE", UNAVAILABLE.getValue());
        map.put("VENUE", VENUE.getValue());

        return map;
    }

    @JsonCreator
    public static CancellationReason forValue(String value) {
        return Arrays.stream(CancellationReason.values())
                .filter(v -> v.getValue().equalsIgnoreCase(value) || v.toString().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new UnknownStatusException(value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
