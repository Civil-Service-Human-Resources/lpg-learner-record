package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.MockedTestConfiguration;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.service.EventService;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EventController.class})
@Import({SpringTestConfiguration.class, MockedTestConfiguration.class})
@AutoConfigureMockMvc
@WithMockUser(username = "user")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Test
    public void shouldReturnEventOnGet() throws Exception {
        String eventUid = "eventUid-id";
        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);
        event.setUid(eventUid);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        mockMvc.perform(
                        get("/event/" + eventUid).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", equalTo(eventUid)))
                .andExpect(jsonPath("$.status", equalTo(EventStatus.CANCELLED.getValue())));

        verify(eventService).findByUid(eventUid, false);
    }


    @Test
    public void shouldReturnEventOnPatch() throws Exception {
        String eventUid = "eventUid-id";

        EventStatusDto eventStatusDto = new EventStatusDto(EventStatus.CANCELLED, CancellationReason.UNAVAILABLE);

        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);
        event.setUid(eventUid);

        when(eventService.updateStatus(eventUid, eventStatusDto)).thenReturn(event);

        mockMvc.perform(
                        patch("/event/" + eventUid).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "CANCELLED",
                                            "cancellationReason": "UNAVAILABLE"
                                        }
                                        """)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", equalTo(eventUid)))
                .andExpect(jsonPath("$.status", equalTo(EventStatus.CANCELLED.getValue())));

        verify(eventService).updateStatus(eventUid, eventStatusDto);
    }

    @Test
    public void shouldReturnNotFoundIfEventNotFoundOnPatch() throws Exception {
        String eventUid = "eventUid-id";
        EventStatusDto eventStatus = new EventStatusDto(EventStatus.CANCELLED, CancellationReason.UNAVAILABLE);

        EventNotFoundException exception = mock(EventNotFoundException.class);

        doThrow(exception).when(eventService).updateStatus(eventUid, eventStatus);

        mockMvc.perform(
                        patch("/event/" + eventUid).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "CANCELLED",
                                            "cancellationReason": "UNAVAILABLE"
                                        }
                                        """)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService).updateStatus(eventUid, eventStatus);
    }

    @Test
    public void shouldReturnNotFoundIfEventNotFoundOnGet() throws Exception {
        String eventUid = "eventUid-id";

        when(eventService.findByUid(eventUid, false)).thenReturn(null);

        mockMvc.perform(
                        get("/event/" + eventUid).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService).findByUid(eventUid, false);
    }

    @Test
    public void shouldCreateEvent() throws Exception {
        EventDto eventDto = new EventDto();

        when(eventService.create(eventDto)).thenReturn(eventDto);

        mockMvc.perform(
                        post("/event").with(csrf())
                                .content(objectMapper.writeValueAsString(eventDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(eventService).create(eventDto);
    }
}
