package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.cslearning.record.Application;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.TestUtils;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.CourseRecordService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DataJpaTest
@WithMockUser(username = "user")
@SpringBootTest(classes = {CourseRecordController.class, CourseRecordService.class, SpringTestConfiguration.class, Application.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@EnableWebMvc
public class CourseRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreateCourseRecord() throws Exception {

        PostCourseRecordInput cr = new PostCourseRecordInput();
        cr.setCourseTitle("Test title");
        cr.setCourseId("createCourseID");
        cr.setUserId("createCourseUserID");
        cr.setIsRequired(true);

        PostModuleRecordInput mr = new PostModuleRecordInput();
        mr.setCourseId("createCourseID");
        mr.setUserId("createCourseUserID");
        mr.setModuleId("createModuleID");
        mr.setDuration(100L);
        mr.setState("IN_PROGRESS");
        mr.setModuleTitle("Test module title");
        mr.setModuleType("elearning");
        mr.setOptional(false);

        cr.setModuleRecords(Collections.singletonList(mr));

        String jsonInput = mapper.writeValueAsString(cr);

        mockMvc.perform(post("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.findByUserId("createCourseUserID").get(0);
        assert (result.isRequired());
        assert (result.getCourseTitle()).equals("Test title");
        assert (result.getCourseId()).equals("createCourseID");
        assertNotNull(result.getLastUpdated());

        ModuleRecord mrResult = result.getModuleRecord("createModuleID");
        assert (mrResult.getDuration()).equals(100L);
        assert (mrResult.getModuleTitle()).equals("Test module title");
        assert (mrResult.getModuleType()).equals("elearning");
        assert (mrResult.getState()).equals(State.IN_PROGRESS);
        assertNotNull(mrResult.getCreatedAt());
        assertNotNull(mrResult.getUpdatedAt());
        assert (!mrResult.getOptional());
    }

    @Test
    public void testGetCourseRecords() throws Exception {
        mockMvc.perform(get("/course_records")
                        .param("courseIds", "testCourse1,testCourse3")
                        .param("userId", "user2"))
                .andExpect(jsonPath("$.courseRecords[0].courseId").value("testCourse1"))
                .andExpect(jsonPath("$.courseRecords[0].userId").value("user2"))
                .andExpect(jsonPath("$.courseRecords[0].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courseRecords[1].courseId").value("testCourse3"))
                .andExpect(jsonPath("$.courseRecords[1].userId").value("user2"))
                .andExpect(jsonPath("$.courseRecords[1].state").value("IN_PROGRESS"));
    }

    @Test
    public void testGetCourseRecord() throws Exception {
        mockMvc.perform(get("/course_records")
                        .param("courseIds", "testCourse1")
                        .param("userId", "user1"))
                .andExpect(jsonPath("$.courseRecords.length()").value(1))
                .andExpect(jsonPath("$.courseRecords[0].courseId").value("testCourse1"))
                .andExpect(jsonPath("$.courseRecords[0].userId").value("user1"))
                .andExpect(jsonPath("$.courseRecords[0].state").value("IN_PROGRESS"));
    }

    @Test
    public void testPatchCourseRecord() throws Exception {
        CourseRecord sampleRecord = new CourseRecord("courseID", "userID");
        sampleRecord.setState(State.IN_PROGRESS);
        courseRecordRepository.save(sampleRecord);
        LocalDateTime datetime = sampleRecord.getLastUpdated();
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        MockHttpServletRequestBuilder builtPatch = TestUtils.buildCourseRecordPatch("courseID", "userID", inputJson);
        mockMvc.perform(builtPatch)
                .andExpect(status().isOk());
        CourseRecord result = courseRecordRepository.findByUserId("userID").get(0);
        assert (result.getState()).equals(State.COMPLETED);
        assertNotEquals(datetime, result.getLastUpdated());
    }

    @Test
    public void testInvalidPatchOperation() throws Exception {
        String inputJson = "[{ \"op\": \"invalid\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        MockHttpServletRequestBuilder builtPatch = TestUtils.buildCourseRecordPatch("courseID", "userID", inputJson);
        mockMvc.perform(builtPatch)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidContentType() throws Exception {
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }\n]";
        MockHttpServletRequestBuilder builtPatch = TestUtils.buildCourseRecordPatch("courseID", "userID", inputJson);
        builtPatch.contentType("application/json");
        mockMvc.perform(builtPatch)
                .andExpect(status().isUnsupportedMediaType());
    }

}
