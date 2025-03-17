package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.CourseRecordIdentity;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.record.TestUtils.assertTime;

@Slf4j
public class CourseRecordControllerTest extends IntegrationTestBase {
    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TestDataService testDataService;
    @Autowired
    private CourseRecordRepository courseRecordRepository;
    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Test
    @Transactional
    public void testCreateCourseRecord() throws Exception {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();

        CourseRecord cr = testDataService.generateCourseRecord(1);

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

        ModuleRecord mrResult = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("testModuleId0")).findFirst().get();
        assert (mrResult.getDuration()).equals(100L);
        assert (mrResult.getModuleTitle()).equals("Test module title");
        assert (mrResult.getModuleType()).equals("elearning");
        assert (mrResult.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assert (!mrResult.getOptional());
    }

    @Test
    @Transactional
    public void testCreateMultipleCourseRecords() throws Exception {
        CourseRecord cr = testDataService.generateCourseRecord(1);
        CourseRecord cr2 = testDataService.generateCourseRecord(1);
        cr2.setIdentity(new CourseRecordIdentity("testCourseId2", "testUserId2"));

        String jsonInput = mapper.writeValueAsString(List.of(cr, cr2));

        mockMvc.perform(post("/course_records/bulk")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.isRequired());
        assert (result.getCourseTitle()).equals("Test title");
        assert (result.getCourseId()).equals("testCourseId");
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("testModuleId0")).findFirst().get();
        assert (mrResult.getDuration()).equals(100L);
        assert (mrResult.getModuleTitle()).equals("Test module title");
        assert (mrResult.getModuleType()).equals("elearning");
        assert (mrResult.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assert (!mrResult.getOptional());

        CourseRecord result2 = courseRecordRepository.findByUserId("testUserId2").get(0);
        assert (result2.isRequired());
        assert (result2.getCourseTitle()).equals("Test title");
        assert (result2.getCourseId()).equals("testCourseId2");
        assertTime(result2.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult2 = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("testModuleId0")).findFirst().get();
        assert (mrResult2.getDuration()).equals(100L);
        assert (mrResult2.getModuleTitle()).equals("Test module title");
        assert (mrResult2.getModuleType()).equals("elearning");
        assert (mrResult2.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult2.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult2.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assert (!mrResult2.getOptional());
    }

    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
    public void testUpdateCourseRecord() throws Exception {

        CourseRecord cr = testDataService.generateCourseRecord(1);
        courseRecordRepository.saveAndFlush(cr);
        String jsonInput = new String(Files.readAllBytes(Paths.get("src/test/resources/courseRecord/update/update-course-record.json")));

        mockMvc.perform(put("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(jsonPath("lastUpdated").value("2023-01-01T10:00:00"))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.APPROVED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);
    }

    /**
     * Should:
     * - Update the course record state to APPROVED
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdateMultipleCourseRecords() throws Exception {

        CourseRecord cr = testDataService.generateCourseRecord(1);
        CourseRecord cr2 = testDataService.generateCourseRecord(1);
        cr2.setIdentity(new CourseRecordIdentity("testCourseId2", "testUserId2"));
        courseRecordRepository.saveAllAndFlush(List.of(cr, cr2));
        String jsonInput = new String(Files.readAllBytes(Paths.get("src/test/resources/courseRecord/update/update-multiple-course-records.json")));

        mockMvc.perform(put("/course_records/bulk")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lastUpdated").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("$[1].lastUpdated").value("2023-01-01T10:00:00"))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.APPROVED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        CourseRecord result2 = courseRecordRepository.findByUserId("testUserId2").get(0);
        assert (result2.getState().equals(State.APPROVED));
        assertTime(result2.getLastUpdated(), 1, 1, 2023, 10, 0, 0);
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
    @Transactional
    public void testUpdateCourseRecordWithModuleRecords() throws Exception {
        CourseRecord cr = testDataService.generateCourseRecord(2);
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
                .andExpect(jsonPath("modules[0].updatedAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("modules[0].createdAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("modules[0].id", Matchers.any(Integer.class)))
                .andExpect(jsonPath("modules[1].moduleId").value("testModuleId1"))
                .andExpect(jsonPath("modules[1].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("modules[1].updatedAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("lastUpdated").value("2023-01-01T10:00:00"))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.APPROVED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        assert result.getModuleRecords().size() == 3;

        ModuleRecord mrResult = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("newModuleRecord")).findFirst().get();
        assert (mrResult.getState()).equals(State.APPROVED);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult2 = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("testModuleId1")).findFirst().get();
        assert (mrResult2.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult2.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
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
    @Transactional
    public void testUpdateCourseRecordAndCreateModuleRecord() throws Exception {
        CourseRecord cr = testDataService.generateCourseRecord(1);
        cr.getModuleRecords().forEach(moduleRecordRepository::saveAndFlush);
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/courseRecord/update/create-one-module.json")));

        mockMvc.perform(put("/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(jsonPath("modules.length()").value(1))
                .andExpect(jsonPath("modules[0].moduleId").value("newModuleRecord"))
                .andExpect(jsonPath("modules[0].state").value("COMPLETED"))
                .andExpect(jsonPath("modules[0].updatedAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("modules[0].createdAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("modules[0].completionDate").value("2024-01-10T10:00:00"))
                .andExpect(jsonPath("modules[0].id", Matchers.any(Integer.class)))
                .andExpect(jsonPath("lastUpdated").value("2023-01-01T10:00:00"))
                .andExpect(status().isOk());

        CourseRecord result = courseRecordRepository.findByUserId("testUserId").get(0);
        assert (result.getState().equals(State.COMPLETED));
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        assertEquals(2, result.getModuleRecords().size());

        ModuleRecord mrResult = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("newModuleRecord")).findFirst().get();
        assert (mrResult.getState()).equals(State.COMPLETED);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCompletionDate(), 10, 1, 2024, 10, 0, 0);
    }

    @Test
    @Transactional
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
    @Transactional
    public void testGetAllCourseRecords() throws Exception {
        mockMvc.perform(get("/course_records")
                        .param("userId", "user2"))
                .andExpect(jsonPath("$.courseRecords[0].courseId").value("testCourse3"))
                .andExpect(jsonPath("$.courseRecords[0].userId").value("user2"))
                .andExpect(jsonPath("$.courseRecords[0].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courseRecords[1].courseId").value("testCourse1"))
                .andExpect(jsonPath("$.courseRecords[1].userId").value("user2"))
                .andExpect(jsonPath("$.courseRecords[1].state").value("IN_PROGRESS"));
    }

    @Test
    @Transactional
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
    @Transactional
    public void testGetCourseRecordWithModules() throws Exception {
        CourseRecord cr = testDataService.generateCourseRecord(2);
        cr.setState(State.IN_PROGRESS);
        cr.getModuleRecords().forEach(moduleRecordRepository::saveAndFlush);
        mockMvc.perform(get("/course_records")
                        .param("courseIds", cr.getCourseId())
                        .param("userId", cr.getUserId()))
                .andExpect(jsonPath("$.courseRecords.length()").value(1))
                .andExpect(jsonPath("$.courseRecords[0].courseId").value(cr.getCourseId()))
                .andExpect(jsonPath("$.courseRecords[0].userId").value(cr.getUserId()))
                .andExpect(jsonPath("$.courseRecords[0].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.courseRecords[0].modules.length()").value(2))
                .andExpect(jsonPath("$.courseRecords[0].modules[0].id", Matchers.any(Integer.class)))
                .andExpect(jsonPath("$.courseRecords[0].modules[1].id", Matchers.any(Integer.class)));
    }

}
