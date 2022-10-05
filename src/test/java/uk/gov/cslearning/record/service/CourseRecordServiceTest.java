package uk.gov.cslearning.record.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.record.api.input.POST.PostCourseRecordInput;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapperImpl;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapperImpl;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
public class CourseRecordServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Captor
    private ArgumentCaptor<CourseRecord> courseRecordArgumentCaptor;

    private CourseRecord getCourseRecord() {
        CourseRecord record = new CourseRecord(
                "courseID",
                "userID"
        );
        record.setLastUpdated(LocalDateTime.now());
        record.setState(State.IN_PROGRESS);
        record.setPreference("DISLIKED");
        return record;
    }

    private PostModuleRecordInput getModuleRecordInput() {
        PostModuleRecordInput recordInput = new PostModuleRecordInput();
        recordInput.setCost(BigDecimal.ONE);
        recordInput.setState("IN_PROGRESS");
        recordInput.setModuleId("moduleID");
        recordInput.setCourseId("courseID");
        recordInput.setUserId("userID");
        recordInput.setModuleType("e-learning");
        recordInput.setModuleTitle("moduleTitle");
        recordInput.setOptional(true);
        return recordInput;
    }

    private PostCourseRecordInput getCourseRecordInput() {
        PostCourseRecordInput recordInput = new PostCourseRecordInput();
        recordInput.setCourseId("courseID");
        recordInput.setState("IN_PROGRESS");
        recordInput.setCourseTitle("testTitle");
        recordInput.setIsRequired(true);
        recordInput.setUserId("userID");
        recordInput.setPreference("LIKED");
        recordInput.setModuleRecords(Lists.emptyList());
        return recordInput;
    }

    private CourseRecordService getService() {
        Clock clock = Clock.fixed(Instant.parse("2022-01-01T00:00:00Z"), ZoneId.of("UTC"));
        ModuleRecordMapper moduleRecordMapper = new ModuleRecordMapperImpl();
        moduleRecordMapper.setClock(clock);

        CourseRecordMapper courseRecordMapper = new CourseRecordMapperImpl();
        courseRecordMapper.setModuleRecordMapper(moduleRecordMapper);
        courseRecordMapper.setClock(clock);

        return new CourseRecordService(
                courseRecordRepository,
                courseRecordMapper,
                new PatchHelper()
        );
    }

    @Test
    @SneakyThrows
    public void testCompleteCourseRecord() {
        CourseRecordService courseRecordService = getService();
        String json = "[" +
                "{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }," +
                "{ \"op\": \"replace\", \"path\": \"/lastUpdated\", \"value\": \"2022-01-01T00:00:00\" }" +
                "\n]";
        JsonPatch patch = JsonPatch.fromJson(mapper.readTree(json));
        CourseRecord record = getCourseRecord();
        Optional<CourseRecord> crOptional = Optional.of(record);
        Mockito.when(courseRecordRepository.getCourseRecord("userID", "courseID")).thenReturn(crOptional);
        courseRecordService.updateCourseRecord("userID", "courseID", patch);

        verify(courseRecordRepository).save(courseRecordArgumentCaptor.capture());
        CourseRecord updatedRecord = courseRecordArgumentCaptor.getValue();
        Assert.assertEquals(State.COMPLETED, updatedRecord.getState());
        LocalDateTime lastUpdated = updatedRecord.getLastUpdated();
        Assert.assertEquals(1, lastUpdated.getMonth().getValue());
        Assert.assertEquals(2022, lastUpdated.getYear());
        Assert.assertEquals(1, lastUpdated.getDayOfMonth());
    }

    @Test
    public void testCreateCourseRecord() {
        PostCourseRecordInput input = getCourseRecordInput();
        CourseRecordService courseRecordService = getService();
        Mockito.when(courseRecordRepository.getCourseRecord("userID", "courseID")).thenReturn(Optional.empty());

        courseRecordService.createCourseRecord(input);
        verify(courseRecordRepository).save(courseRecordArgumentCaptor.capture());
        CourseRecord createdRecord = courseRecordArgumentCaptor.getValue();
        Assert.assertEquals(State.IN_PROGRESS, createdRecord.getState());
        Assert.assertEquals("2022-01-01T00:00", createdRecord.getLastUpdated().toString() );
        Assert.assertTrue(createdRecord.isRequired());
        Assert.assertEquals("courseID", createdRecord.getCourseId());
        Assert.assertEquals("testTitle", createdRecord.getCourseTitle());
        Assert.assertEquals("userID", createdRecord.getUserId());
        Assert.assertEquals("LIKED", createdRecord.getPreference());
    }

    @Test
    public void testCreateCourseRecordWithModuleRecord() {
        PostCourseRecordInput input = getCourseRecordInput();
        PostModuleRecordInput moduleRecordInput = getModuleRecordInput();
        input.setModuleRecords(Collections.singletonList(moduleRecordInput));
        CourseRecordService courseRecordService = getService();
        Mockito.when(courseRecordRepository.getCourseRecord("userID", "courseID")).thenReturn(Optional.empty());

        courseRecordService.createCourseRecord(input);
        verify(courseRecordRepository).save(courseRecordArgumentCaptor.capture());
        CourseRecord createdCourseRecord = courseRecordArgumentCaptor.getValue();
        ModuleRecord createdRecord = createdCourseRecord.getModuleRecord("moduleID");

        Assert.assertEquals(State.IN_PROGRESS, createdRecord.getState());
        Assert.assertEquals("2022-01-01T00:00", createdRecord.getCreatedAt().toString() );
        Assert.assertEquals("2022-01-01T00:00", createdRecord.getUpdatedAt().toString() );
        Assert.assertTrue(createdRecord.getOptional());
        Assert.assertEquals("courseID", createdRecord.getCourseRecord().getCourseId());
        Assert.assertEquals("testTitle", createdRecord.getCourseRecord().getCourseTitle());
        Assert.assertEquals("userID", createdRecord.getCourseRecord().getUserId());
        Assert.assertEquals("e-learning", createdRecord.getModuleType());
        Assert.assertEquals("moduleTitle", createdRecord.getModuleTitle());
    }
}
