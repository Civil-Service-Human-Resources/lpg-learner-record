package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.CourseRecord;
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
public class ModuleRecordControllerTest extends IntegrationTestBase {
    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TestDataService testDataService;
    @Autowired
    private CourseRecordRepository courseRecordRepository;
    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Test
    @Transactional
    public void testCreateModuleRecordExistingCourseRecord() throws Exception {
        String courseId = testDataService.getCourseId();
        String userId = testDataService.getUserId();

        CourseRecord cr = testDataService.generateCourseRecord(0);
        courseRecordRepository.save(cr);
        ModuleRecord testMr = testDataService.generateModuleRecord();

        String jsonInput = mapper.writeValueAsString(testMr);

        mockMvc.perform(post("/module_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        CourseRecord result = courseRecordRepository.findByUserId(userId).get(0);
        assert (result.isRequired());
        assert (result.getCourseTitle()).equals("Test title");
        assert (result.getCourseId()).equals(courseId);
        assertTime(result.getLastUpdated(), 1, 1, 2023, 10, 0, 0);

        ModuleRecord mrResult = result.getModuleRecords().stream().filter(mr -> mr.getModuleId().equals("testModuleId")).findFirst().get();
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
    public void testCreateMultipleModuleRecords() throws Exception {
        ModuleRecord mr = testDataService.generateModuleRecord();
        ModuleRecord mr2 = testDataService.generateModuleRecord();
        mr2.setModuleId("testModuleId2");

        String jsonInput = mapper.writeValueAsString(List.of(mr, mr2));

        mockMvc.perform(post("/module_records/bulk")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isCreated());

        List<ModuleRecord> moduleRecordList = moduleRecordRepository.findByUserIdAndModuleIdIn(List.of("testUserId"), null);
        assertEquals(2, moduleRecordList.size());


        ModuleRecord mrResult = moduleRecordList.get(0);
        assert (mrResult.getDuration()).equals(100L);
        assert (mrResult.getModuleTitle()).equals("Test module title");
        assert (mrResult.getModuleType()).equals("elearning");
        assert (mrResult.getState()).equals(State.IN_PROGRESS);
        assertTime(mrResult.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(mrResult.getCreatedAt(), 1, 1, 2023, 10, 0, 0);
        assert (!mrResult.getOptional());

        ModuleRecord mrResult2 = moduleRecordList.get(1);
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
    public void testCreateCourseRecordInvalidParams() throws Exception {
        String testDataDir = "src/test/resources/moduleRecord/create/invalid";
        try (Stream<Path> pathsStream = Files.list(Paths.get(testDataDir))) {
            for (Path filePath : pathsStream.collect(Collectors.toList())) {
                String json = new String(Files.readAllBytes(filePath));
                log.info(String.format("Sending JSON '%s'", json));
                mockMvc.perform(post("/module_records")
                                .with(csrf())
                                .contentType("application/json")
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    /**
     * Should:
     * - Update the module record state to APPROVED
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdateMultipleModuleRecords() throws Exception {

        String jsonInput = new String(Files.readAllBytes(Paths.get("src/test/resources/moduleRecord/update/update-multiple-module-records.json")));

        mockMvc.perform(put("/module_records/bulk")
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonInput))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleRecords.length()").value(2))
                .andExpect(jsonPath("$.moduleRecords[0].updatedAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("$.moduleRecords[0].state").value("APPROVED"))
                .andExpect(jsonPath("$.moduleRecords[1].updatedAt").value("2023-01-01T10:00:00"))
                .andExpect(jsonPath("$.moduleRecords[1].state").value("APPROVED"));
    }

    @Test
    @Transactional
    public void testGetCourseRecords() throws Exception {
        mockMvc.perform(get("/module_records")
                        .param("moduleIds", "testModule1,testModule2")
                        .param("userIds", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleRecords[0].moduleId").value("testModule1"))
                .andExpect(jsonPath("$.moduleRecords[0].userId").value("user1"))
                .andExpect(jsonPath("$.moduleRecords[0].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.moduleRecords[1].moduleId").value("testModule2"))
                .andExpect(jsonPath("$.moduleRecords[1].userId").value("user1"))
                .andExpect(jsonPath("$.moduleRecords[1].state").value("IN_PROGRESS"));
    }

    @Test
    @Transactional
    public void testGetAllCourseRecords() throws Exception {
        mockMvc.perform(get("/module_records")
                        .param("userIds", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleRecords[0].moduleId").value("testModule1"))
                .andExpect(jsonPath("$.moduleRecords[0].userId").value("user1"))
                .andExpect(jsonPath("$.moduleRecords[0].state").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.moduleRecords[1].moduleId").value("testModule2"))
                .andExpect(jsonPath("$.moduleRecords[1].userId").value("user1"))
                .andExpect(jsonPath("$.moduleRecords[1].state").value("IN_PROGRESS"));
    }

    @Test
    @Transactional
    public void testGetCourseRecord() throws Exception {
        mockMvc.perform(get("/module_records")
                        .param("moduleIds", "testModule1")
                        .param("userIds", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleRecords.length()").value(1))
                .andExpect(jsonPath("$.moduleRecords[0].moduleId").value("testModule1"))
                .andExpect(jsonPath("$.moduleRecords[0].userId").value("user1"))
                .andExpect(jsonPath("$.moduleRecords[0].state").value("IN_PROGRESS"));
    }

}
