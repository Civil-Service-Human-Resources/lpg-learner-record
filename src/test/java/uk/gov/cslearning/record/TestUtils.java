package uk.gov.cslearning.record;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static void assertTime(LocalDateTime datetime, int expDay, int expMonth, int expYear,
                                  int expHour, int expMinute, int expSecond) {
        assertEquals(expDay, datetime.getDayOfMonth());
        assertEquals(expMonth, datetime.getMonthValue());
        assertEquals(expYear, datetime.getYear());
        assertEquals(expHour, datetime.getHour());
        assertEquals(expMinute, datetime.getMinute());
        assertEquals(expSecond, datetime.getSecond());
    }

}
