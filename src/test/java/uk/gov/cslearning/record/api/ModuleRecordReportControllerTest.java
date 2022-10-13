package uk.gov.cslearning.record.api;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.service.ModuleRecordService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest({ModuleRecordReportController.class})
@Import(SpringTestConfiguration.class)
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class ModuleRecordReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleRecordService moduleRecordService;

    @Test
    public void shouldReturnListOfModuleRecordDtos() throws Exception {
        String from = "2018-01-01";
        String to = "2018-12-31";
        String moduleId = "module-id";
        String state = "Completed";

        ModuleRecordDto moduleRecordDto = new ModuleRecordDto();

        moduleRecordDto.setModuleId(moduleId);
        moduleRecordDto.setState(state);

        List<ModuleRecordDto> results = Lists.newArrayList(moduleRecordDto);

        when(moduleRecordService.listRecordsForPeriod(LocalDate.parse(from), LocalDate.parse(to))).thenReturn(results);

        mockMvc.perform(get("/reporting/module-records")
                .param("from", from)
                .param("to", to)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[0].moduleId", equalTo(moduleId)))
                .andExpect(jsonPath("$.[0].state", equalTo(state)));
    }
}
