package uk.gov.cslearning.record.api;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.dto.CourseRecordDto;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.service.CourseRecordService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest({CourseRecordController.class, ErrorDtoFactory.class})
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class CourseRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRecordService courseRecordService;

    @Test
    public void shouldReturnListOfCourseRecordDtos() throws Exception {
        String from = "2018-01-01";
        String to = "2018-12-31";
        String courseId = "course-id";
        String state = "Completed";

        CourseRecordDto courseRecordDto = new CourseRecordDto();

        courseRecordDto.setCourseId(courseId);
        courseRecordDto.setState(state);

        List<CourseRecordDto> results = Lists.newArrayList(courseRecordDto);

        when(courseRecordService.listRecordsForPeriod(LocalDate.parse(from), LocalDate.parse(to))).thenReturn(results);

        mockMvc.perform(get("/reporting/course-records")
                .param("from", from)
                .param("to", to)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[0].courseId", equalTo(courseId)))
                .andExpect(jsonPath("$.[0].state", equalTo(state)));
    }
}