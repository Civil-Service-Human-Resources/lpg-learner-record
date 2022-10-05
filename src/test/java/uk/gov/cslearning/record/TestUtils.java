package uk.gov.cslearning.record;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

public class TestUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static JsonPatch generatePatch(String rawJson) throws IOException {
        JsonNode patchJson = mapper.readTree(rawJson);
        return JsonPatch.fromJson(patchJson);
    }

    public static MockHttpServletRequestBuilder buildPatch(String content) {
        return patch("/course_records")
                .with(csrf())
                .contentType("application/json-patch+json")
                .param("userId", "testUserId")
                .param("courseId", "testCourseId")
                .content(content);
    }
}
