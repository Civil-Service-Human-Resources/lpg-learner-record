package uk.gov.cslearning.record.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.dto.EventDto;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.dto.EventStatusDto;
import uk.gov.cslearning.record.dto.factory.ErrorDtoFactory;
import uk.gov.cslearning.record.exception.EventNotFoundException;
import uk.gov.cslearning.record.service.EventService;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({EventController.class, ErrorDtoFactory.class})
@WithMockUser(username = "user")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Test
    public void shouldReturnEventOnGet() throws Exception {
        String eventUid = "event-id";
        URI uri = URI.create("http://localhost:9001/courses/course-id/modules/module-id/events/event-id");

        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);
        event.setUid(eventUid);
        event.setUri(uri);

        when(eventService.findByUid(eventUid, false)).thenReturn(event);

        mockMvc.perform(
                get("/event/" + eventUid).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", equalTo(eventUid)))
                .andExpect(jsonPath("$.status", equalTo(EventStatus.CANCELLED.getValue())))
                .andExpect(jsonPath("$.uri", equalTo(uri.toString())));

        verify(eventService).findByUid(eventUid);
    }


    @Test
    public void shouldReturnEventOnPatch() throws Exception {
        String eventUid = "event-id";
        URI uri = URI.create("http://localhost:9001/courses/course-id/modules/module-id/events/event-id");

        EventStatusDto eventStatusDto = new EventStatusDto(EventStatus.CANCELLED, "");

        EventDto event = new EventDto();
        event.setStatus(EventStatus.CANCELLED);
        event.setUid(eventUid);
        event.setUri(uri);

        when(eventService.updateStatus(eventUid, eventStatusDto)).thenReturn(Optional.of(event));

        mockMvc.perform(
                patch("/event/" + eventUid).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventStatusDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", equalTo(eventUid)))
                .andExpect(jsonPath("$.status", equalTo(EventStatus.CANCELLED.getValue())))
                .andExpect(jsonPath("$.uri", equalTo(uri.toString())));

        verify(eventService).updateStatus(eventUid, eventStatusDto);
    }

    @Test
    public void shouldReturnNotFoundIfEventNotFoundOnPatch() throws Exception {
        String eventUid = "event-id";
        EventStatusDto eventStatus = new EventStatusDto(EventStatus.CANCELLED, "");

        EventNotFoundException exception = mock(EventNotFoundException.class);

        doThrow(exception).when(eventService).updateStatus(eventUid, eventStatus);

        mockMvc.perform(
                patch("/event/" + eventUid).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventStatus))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService).updateStatus(eventUid, eventStatus);
    }

    @Test
    public void shouldReturnNotFoundIfEventNotFoundOnGet() throws Exception {
        String eventUid = "event-id";

        when(eventService.findByUid(eventUid, false)).thenReturn(null);

        mockMvc.perform(
                get("/event/" + eventUid).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService).findByUid(eventUid);
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
