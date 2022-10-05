package uk.gov.cslearning.record.api.util;

import com.github.fge.jsonpatch.JsonPatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.cslearning.record.SpringTestConfiguration;
import uk.gov.cslearning.record.TestUtils;
import uk.gov.cslearning.record.api.CourseRecordController;
import uk.gov.cslearning.record.api.input.PATCH.PatchCourseRecordInput;
import uk.gov.cslearning.record.api.input.PATCH.PatchModuleRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapperImpl;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapperImpl;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CourseRecordMapperImpl.class, ModuleRecordMapperImpl.class})
public class JsonPatchTest {

    @Autowired
    private CourseRecordMapper courseRecordMapper;

    @Test
    public void testSuccessfulCourseRecordPatch() throws Exception {
        CourseRecord sampleRecord = new CourseRecord();
        sampleRecord.setState(State.IN_PROGRESS);
        PatchCourseRecordInput cri = new PatchCourseRecordInput();
        cri.setState("COMPLETED");
        courseRecordMapper.update(sampleRecord, cri);
    }
}
