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
import uk.gov.cslearning.record.repository.ModuleRecordRepository;
import uk.gov.cslearning.record.service.CourseRecordService;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.record.TestUtils.assertTime;


@DataJpaTest
@WithMockUser(username = "user")
@SpringBootTest(classes = {ModuleRecordController.class, ModuleRecordService.class, SpringTestConfiguration.class, Application.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@EnableWebMvc
public class ModuleRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Autowired
    private CourseRecordRepository courseRecordRepository;


    private final static ObjectMapper mapper = new ObjectMapper();

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
        assertTime(result.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(result.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertFalse(result.getOptional());
    }

    @Test
    public void testPatchModuleRecord() throws Exception {
        String inputJson = "[{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }, { \"op\": \"replace\", \"path\": \"/completionDate\", \"value\": \"2023-02-02T10:00:00.000Z\" }\n]";
        MockHttpServletRequestBuilder builtPatch = TestUtils.buildModuleRecordPatch("1001", inputJson);
        mockMvc.perform(builtPatch)
                .andExpect(status().isOk());

        ModuleRecord result = moduleRecordRepository.findById(1001L).get();
        assert(result.getState()).equals(State.COMPLETED);
        assertTime(result.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(result.getCompletionDate(), 2, 2, 2023, 10, 0, 0);

        CourseRecord courseRecord = courseRecordRepository.findByUserIdAndCourseIdIn("user1",Collections.singletonList("testCourse1")).get(0);
    }

}
