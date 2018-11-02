package uk.gov.cslearning.record.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;

import javax.ws.rs.core.MediaType;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

public class EventControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private EventController controller;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private InviteRepository inviteRepository;

    @Before
    public void setup(){
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldAddInvitee() throws Exception{
        Event event = new Event();
        event.setCatalogueId("SAI");
        event.setPath("test/path");

        when(eventRepository.findByCatalogueId("SAI")).thenReturn(Optional.of(event));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/event/SAI/invitee").with(csrf())
                .content("{\"learnerEmail\": \"user@test.com\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnBadRequestIfNoEmailIsPresent() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.post("/event/SAI/invitee").with(csrf())
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
