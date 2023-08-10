package uk.gov.cslearning.record;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

public class TestUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static JsonPatch generatePatch(String rawJson) throws IOException {
        JsonNode patchJson = mapper.readTree(rawJson);
        return JsonPatch.fromJson(patchJson);
    }

    public static MockHttpServletRequestBuilder buildCourseRecordPatch(String courseId, String userId, String content) {
        return patch("/course_records")
                .with(csrf())
                .contentType("application/json-patch+json")
                .param("courseId", courseId)
                .param("userId", userId)
                .content(content);
    }

    public static MockHttpServletRequestBuilder buildModuleRecordPatch(String mrId, String content) {
        return patch("/module_records/" + mrId)
                .with(csrf())
                .contentType("application/json-patch+json")
                .content(content);
    }

    public static void assertTime(LocalDateTime datetime, int day, int month, int year, int hour, int minute, int second) {
        assertEquals(day, datetime.getDayOfMonth());
        assertEquals(month, datetime.getMonthValue());
        assertEquals(year, datetime.getYear());
        assertEquals(minute, datetime.getMinute());
        assertEquals(second, datetime.getSecond());
        assertEquals(hour, datetime.getHour());
    }

    public static void assertDate(LocalDate date, int day, int month, int year) {
        assertEquals(day, date.getDayOfMonth());
        assertEquals(month, date.getMonthValue());
        assertEquals(year, date.getYear());
    }
}
