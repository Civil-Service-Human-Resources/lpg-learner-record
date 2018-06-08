package uk.gov.cslearning.record.api;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.ActivityRecordService;
import uk.gov.cslearning.record.service.UserRecordService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class EventRegistrationsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private EventRegistrationsController controller;

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnZeroIfNoRegistrations() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/registrations/abc/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(0));
    }

    @Test
    public void shouldReturnRecords() throws Exception {

        when(courseRecordRepository.countRegisteredForEvent("abc")).thenReturn(5);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/registrations/abc/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(5));
    }
}
