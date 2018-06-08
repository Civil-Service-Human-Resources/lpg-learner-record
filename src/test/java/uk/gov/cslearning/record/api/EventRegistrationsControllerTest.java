package uk.gov.cslearning.record.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

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
                MockMvcRequestBuilders.get("/registrations/count?eventId=abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("abc"))
                .andExpect(jsonPath("$[0].value").value(0));
    }

    @Test
    public void shouldReturnCountValue() throws Exception {

        when(courseRecordRepository.countRegisteredForEvent("abc")).thenReturn(5);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/registrations/count?eventId=abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("abc"))
                .andExpect(jsonPath("$[0].value").value(5));
    }

    @Test
    public void shouldReturnMultipleCountValues() throws Exception {

        when(courseRecordRepository.countRegisteredForEvent("abc")).thenReturn(5);
        when(courseRecordRepository.countRegisteredForEvent("def")).thenReturn(0);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/registrations/count?eventId=abc&eventId=def")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("abc"))
                .andExpect(jsonPath("$[0].value").value(5))
                .andExpect(jsonPath("$[1].eventId").value("def"))
                .andExpect(jsonPath("$[1].value").value(0));
    }
}
