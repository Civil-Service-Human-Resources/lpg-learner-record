package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.bson.json.JsonParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.CourseRecordService;

import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({CourseRecordController.class})
@Import(SpringTestConfiguration.class)
@WithMockUser(username = "user")
public class CourseRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRecordService courseRecordService;

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonPatch generatePatch(String rawJson) throws IOException {
        JsonNode patchJson = mapper.readTree(rawJson);
        return JsonPatch.fromJson(patchJson);
    }

    private MockHttpServletRequestBuilder buildPatch(String content) {
        MockHttpServletRequestBuilder builtPatch = patch("/course_records")
                .with(csrf())
                .contentType("application/json-patch+json")
                .param("userId", "testUserId")
                .param("courseId", "testCourseId")
                .content(content);
        return builtPatch;
    }

    @Test
    public void testSuccessfulPatchInput() throws Exception {
        CourseRecord sampleRecord = new CourseRecord();
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        JsonPatch patchJson = generatePatch(inputJson);
        when(courseRecordService.updateCourseRecord("testUserId", "testCourseId", patchJson))
                .thenReturn(sampleRecord);
        MockHttpServletRequestBuilder builtPatch = buildPatch(inputJson);
        mockMvc.perform(builtPatch)
                .andExpect(status().isOk());
    }

    @Test
    public void testInvalidPatchOperation() throws Exception {
        String inputJson = "[{ \"op\": \"invalid\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        MockHttpServletRequestBuilder builtPatch = buildPatch(inputJson);
        mockMvc.perform(builtPatch)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidContentType() throws Exception {
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        MockHttpServletRequestBuilder builtPatch = buildPatch(inputJson);
        builtPatch.contentType("application/json");
        mockMvc.perform(builtPatch)
                .andExpect(status().isUnsupportedMediaType());
    }
}
