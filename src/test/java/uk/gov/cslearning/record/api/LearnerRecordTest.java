package uk.gov.cslearning.record.api;

import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.LearnerRecordService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class LearnerRecordTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LearnerRecord controller;

    @Mock
    private LearnerRecordService learnerRecordService;

    @BeforeTest
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnEmptyListForInvalidUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/record/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isEmpty());
    }

    @Test
    public void shouldReturnRecords() throws Exception {

        when(learnerRecordService.getLearnerRecord("1", null))
                .thenReturn(ImmutableList.of(new Record("activityId", "complete", null, null, LocalDateTime.now())));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/record/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records", hasSize(1)))
                .andExpect(jsonPath("$.records[0].state").value("complete"));
    }
}
