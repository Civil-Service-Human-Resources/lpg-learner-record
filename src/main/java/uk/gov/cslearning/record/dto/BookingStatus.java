package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.cslearning.record.exception.UnknownBookingStatusException;

import java.util.Arrays;

public enum BookingStatus {
    REQUESTED("Requested"), CONFIRMED("Confirmed");

    private String value;

    BookingStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static BookingStatus forValue(String value) {
        return Arrays.stream(BookingStatus.values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new UnknownBookingStatusException(value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
