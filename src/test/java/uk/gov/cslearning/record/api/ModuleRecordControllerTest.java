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
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.record.TestUtils.assertTime;


@DataJpaTest
@WithMockUser(username = "user")
@SpringBootTest(classes = {ModuleRecordController.class, ModuleRecordService.class, SpringTestConfiguration.class, Application.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@EnableWebMvc
public class ModuleRecordControllerTest {

    private final static ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ModuleRecordRepository moduleRecordRepository;
    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void testCreateModuleRecord() throws Exception {

        PostModuleRecordInput mr = new PostModuleRecordInput();
        mr.setCourseId("testCourse1");
        mr.setUserId("user1");
        mr.setModuleId("createModuleID");
        mr.setDuration(100L);
        mr.setState("IN_PROGRESS");
        mr.setModuleTitle("Test module title");
        mr.setModuleType("elearning");
        mr.setOptional(false);

        String jsonInput = mapper.writeValueAsString(mr);

        LocalDateTime crUpdated = courseRecordRepository.getCourseRecord("user1", "testCourse1").get().getLastUpdated();

        mockMvc.perform(post("/module_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        ModuleRecord result = moduleRecordRepository.findAll()
                .stream()
                .filter(m -> m.getModuleId().equals("createModuleID"))
                .findFirst()
                .orElseThrow(() -> new Exception("createModuleID module not found"));
        assertEquals(100L, result.getDuration().longValue());
        assertEquals("Test module title", result.getModuleTitle());
        assertEquals("elearning", result.getModuleType());
        assertEquals(State.IN_PROGRESS, result.getState());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertFalse(result.getOptional());

        CourseRecord resultCr = courseRecordRepository.getCourseRecord("user1", "testCourse1").get();
        assertNotEquals(crUpdated, resultCr.getLastUpdated());
    }

    @Test()
    public void testCreateExistingModuleRecord() throws Exception {

        PostModuleRecordInput mr = new PostModuleRecordInput();
        mr.setCourseId("testCourse1");
        mr.setUserId("user1");
        mr.setModuleId("testModule1");
        mr.setDuration(100L);
        mr.setState("IN_PROGRESS");
        mr.setModuleTitle("Test module title");
        mr.setModuleType("elearning");
        mr.setOptional(false);

        String jsonInput = mapper.writeValueAsString(mr);
        mockMvc.perform(post("/module_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchModuleRecord() throws Exception {
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }, { \"op\": \"replace\", \"path\": \"/completionDate\", \"value\": \"2023-02-02T10:00:00.000Z\" }\n]";
        MockHttpServletRequestBuilder builtPatch = TestUtils.buildModuleRecordPatch("1001", inputJson);
        LocalDateTime mrUpdated = moduleRecordRepository.findById(1001L).get().getUpdatedAt();
        LocalDateTime crUpdated = courseRecordRepository.findByUserIdAndCourseIdIn("user1", Collections.singletonList("testCourse1")).get(0).getLastUpdated();
        mockMvc.perform(builtPatch)
                .andExpect(status().isOk());

        ModuleRecord result = moduleRecordRepository.findById(1001L).get();
        assert (result.getState()).equals(State.COMPLETED);
        assertNotEquals(mrUpdated, result.getUpdatedAt());
        assertTime(result.getCompletionDate(), 2, 2, 2023, 10, 0, 0);

        CourseRecord courseRecord = courseRecordRepository.findByUserIdAndCourseIdIn("user1", Collections.singletonList("testCourse1")).get(0);
        assertNotEquals(crUpdated, courseRecord.getLastUpdated());
    }

}
