package uk.gov.cslearning.record.api;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.MockedTestConfiguration;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.BookingService;
import uk.gov.cslearning.record.service.EventService;
import uk.gov.cslearning.record.service.InviteService;
import uk.gov.cslearning.record.service.identity.IdentitiesService;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({InviteController.class})
@Import({SpringTestConfiguration.class, MockedTestConfiguration.class})
@AutoConfigureMockMvc
@WithMockUser(username = "user")
public class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InviteService inviteService;

    @MockBean
    private IdentitiesService identityService;

    @MockBean
    private BookingService bookingService;
    @MockBean
    private EventService eventService;

    @Test
    public void shouldGetAllInvitees() throws Exception {
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(1);

        ArrayList<InviteDto> invites = new ArrayList<>();
        invites.add(inviteDto);

        when(inviteService.findByEventId("SGAI")).thenReturn(invites);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/event/SGAI/invitee").with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetInvitee() throws Exception {
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(99);

        when(inviteService.findInvite(99)).thenReturn(Optional.of(inviteDto));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/event/SGI/invitee/99").with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(99)));

    }

}
