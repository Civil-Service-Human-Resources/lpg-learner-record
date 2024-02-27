package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.cslearning.record.Application;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;
import uk.gov.cslearning.record.service.CourseRecordService;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.record.TestUtils.assertTime;


@DataJpaTest
@WithMockUser(username = "user")
@SpringBootTest(classes = {CourseRecordController.class, CourseRecordService.class, ModuleRecordService.class,
        SpringTestConfiguration.class, Application.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@EnableWebMvc
@Slf4j
public class CourseRecordControllerTest extends TestDataService {

    private final static ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourseRecordRepository courseRecordRepository;
    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Test
    public void testCreateCourseRecord() throws Exception {

        CourseRecord cr = generateCourseRecord(1);

        String jsonInput = mapper.writeValueAsString(cr);

        mockMvc.perform(post("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.findByUserId(userId).get(0);
        assert (result.isRequired());
        assert (result.getCourseTitle()).equals("Test title");
        assert (result.getCourseId()).equals(courseId);
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult = result.getModuleRecord("testModuleId0");
        assert (mrResult.getDuration()).equals(100L);
        assert (mrResult.getModuleTitle()).equals("Test module title");
        assert (mrResult.getModuleType()).equals("elearning");
        assert (mrResult.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assert (!mrResult.getOptional());
    }

    @Test
    public void testUpdateCourseRecordInvalidParams() throws Exception {
        String testDataDir = "src/test/resources/courseRecord/update/invalid";
        try (Stream<Path> pathsStream = Files.list(Paths.get(testDataDir))) {
            for (Path filePath : pathsStream.collect(Collectors.toList())) {
                String json = new String(Files.readAllBytes(filePath));
                log.info(String.format("Sending JSON '%s'", json));
                mockMvc.perform(put("/course_records")
                                .with(csrf())
                                .contentType("application/json")
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Test
    public void testCreateCourseRecordInvalidParams() throws Exception {
        String testDataDir = "src/test/resources/courseRecord/create/invalid";
        try (Stream<Path> pathsStream = Files.list(Paths.get(testDataDir))) {
            for (Path filePath : pathsStream.collect(Collectors.toList())) {
                String json = new String(Files.readAllBytes(filePath));
                log.info(String.format("Sending JSON '%s'", json));
                mockMvc.perform(post("/course_records")
                                .with(csrf())
                                .contentType("application/json")
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    /**
     * Should:
     * - Update the course record state to APPROVED
     *
     * @throws Exception
     */
    @Test
    public void testUpdateCourseRecord() throws Exception {

        CourseRecord cr = generateCourseRecord(1);
        courseRecordRepository.saveAndFlush(cr);
        String jsonInput = new String(Files.readAllBytes(Paths.get("src/test/resources/courseRecord/update/update-course-record.json")));

        mockMvc.perform(put("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(jsonPath("lastUpdated", Matchers.contains(2023, 1, 1, 10, 0)))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.APPROVED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);
    }

    /**
     * Should:
     * - Update the course record state to APPROVED
     * - Update the module record state to APPROVED
     * - Create a new module record with state IN_PROGRESS
     *
     * @throws Exception
     */
    @Test
    public void testUpdateCourseRecordWithModuleRecords() throws Exception {
        CourseRecord cr = generateCourseRecord(2);
        List<ModuleRecord> mrs = cr.getModuleRecords().stream().map(moduleRecordRepository::saveAndFlush)
                .collect(Collectors.toList());
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/courseRecord/update/update-multiple-modules.json")));
        Long moduleRecordIdToUpdate = mrs.stream().filter(mr -> mr.getModuleId().equals("testModuleId1")).findFirst().get().getId();
        json = String.format(json, moduleRecordIdToUpdate);

        mockMvc.perform(put("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(jsonPath("modules.length()").value(2))
                .andExpect(jsonPath("modules[0].moduleId").value("newModuleRecord"))
                .andExpect(jsonPath("modules[0].state").value("APPROVED"))
                .andExpect(jsonPath("modules[0].updatedAt", Matchers.contains(2023, 1, 1, 10, 0)))
                .andExpect(jsonPath("modules[0].createdAt", Matchers.contains(2023, 1, 1, 10, 0)))
                .andExpect(jsonPath("modules[1].moduleId").value("testModuleId1"))
                .andExpect(jsonPath("modules[1].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("modules[1].updatedAt", Matchers.contains(2023, 1, 1, 10, 0)))
                .andExpect(jsonPath("lastUpdated", Matchers.contains(2023, 1, 1, 10, 0)))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.APPROVED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        assert result.getModuleRecords().size() == 3;

        ModuleRecord mrResult = result.getModuleRecord("newModuleRecord");
        assert (mrResult.getState()).equals(State.APPROVED);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult2 = result.getModuleRecord("testModuleId1");
        assert (mrResult2.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult2.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
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

}
