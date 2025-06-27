package uk.gov.cslearning.record.integration.record;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CourseRecordTest extends IntegrationTestBase {

    @Test
    public void testCreateCourseRecord() throws Exception {
        String json = """
                {
                    "resourceId": "course-id",
                    "learnerId": "user-id"
                }
                """;
        mockMvc.perform(post("/v2/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("recordType.type").value("COURSE"))
                .andExpect(jsonPath("resourceId").value("course-id"))
                .andExpect(jsonPath("createdTimestamp").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("learnerId").value("user-id"));
    }

    @Test
    public void testCreateCourseRecordParentValidation() throws Exception {
        String json = """
                {
                    "resourceId": "course-id",
                    "learnerId": "user-id",
                    "parentId": 2
                }
                """;
        mockMvc.perform(post("/v2/course_records")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].fieldName").value("parentId"))
                .andExpect(jsonPath("errors[0].error").value("must be null"));
    }

    @Test
    public void testGetBulkCourseRecords() throws Exception {
        mockMvc.perform(get("/v2/course_records")
                        .param("learnerIds", "user2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.length()").value(2))
                .andExpect(jsonPath("totalElements").value(2));
    }

}
