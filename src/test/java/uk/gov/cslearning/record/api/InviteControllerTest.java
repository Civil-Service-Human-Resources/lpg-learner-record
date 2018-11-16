package uk.gov.cslearning.record.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.service.InviteService;

import javax.ws.rs.core.MediaType;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.hamcrest.Matchers.equalTo;

public class InviteControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private InviteController controller;

    @Mock
    private InviteService inviteService;

    @Before
    public void setup(){
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldGetAllInvitees() throws Exception{
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
    public void shouldGetInvitee() throws Exception{
        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(99);

        when(inviteService.findInvite(99)).thenReturn(inviteDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/event/SGI/invitee/99").with(csrf())
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(99)));

    }

    @Test
    public void shouldAddInvitee() throws Exception{
        when(inviteService.save(any())).thenReturn(new InviteDto());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/event/SAI/invitee").with(csrf())
                .content("{\"learnerEmail\": \"user@test.com\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
