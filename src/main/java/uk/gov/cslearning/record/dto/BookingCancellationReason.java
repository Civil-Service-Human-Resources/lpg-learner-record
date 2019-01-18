package uk.gov.cslearning.record.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.cslearning.record.exception.UnknownStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BookingCancellationReason {
    PAYMENT("the booking has not been paid"),
    REQUESTED("the learner has requested that the booking be cancelled"),
    BEREAVEMENT("Family bereavement"),
    ILLNESS("Illness"),
    PRIORITIES("Other work priorities");

    private final String value;

    BookingCancellationReason(String value) {
        this.value = value;
    }

    @JsonCreator
    public static BookingCancellationReason forValue(String value) {
        return Arrays.stream(BookingCancellationReason.values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new UnknownStatusException(value));
    }

    public static Map<String, String> getManagementKeyValuePairs(){
        Map<String, String> map = new HashMap<>();
        map.put("PAYMENT", PAYMENT.getValue());
        map.put("REQUESTED", REQUESTED.getValue());

        return map;
    }

    public static Map<String, String> getUserKeyValuePairs(){
        Map<String, String> map = new HashMap<>();
        map.put(BEREAVEMENT.name(), BEREAVEMENT.getValue());
        map.put(ILLNESS.name(), ILLNESS.getValue());
        map.put(PRIORITIES.name(), PRIORITIES.getValue());

        return map;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}