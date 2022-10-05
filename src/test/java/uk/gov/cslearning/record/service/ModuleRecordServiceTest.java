package uk.gov.cslearning.record.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.api.input.POST.PostModuleRecordInput;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapper;
import uk.gov.cslearning.record.api.mapper.CourseRecordMapperImpl;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapperImpl;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.*;
import uk.gov.cslearning.record.dto.ModuleRecordDto;
import uk.gov.cslearning.record.dto.factory.ModuleRecordDtoFactory;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleRecordServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private ModuleRecordRepository moduleRecordRepository;

    @Captor
    private ArgumentCaptor<ModuleRecord> moduleRecordArgumentCaptor;

    @Captor
    private ArgumentCaptor<CourseRecord> courseRecordArgumentCaptor;

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

    private CourseRecord getCourseRecord() {
        CourseRecord record = new CourseRecord("courseID", "userID");
        record.setCourseTitle("testTitle");
        return record;
    }

    private ModuleRecord getModuleRecord() {
        ModuleRecord record = new ModuleRecord();
        record.setId(10000L);
        record.setCost(BigDecimal.ONE);
        record.setState(State.IN_PROGRESS);
        record.setModuleId("moduleID");
        record.setCourseRecord(getCourseRecord());
        record.setModuleType("e-learning");
        record.setModuleTitle("moduleTitle");
        record.setOptional(true);
        return record;
    }

    private ModuleRecordService getService() {
        Clock clock = Clock.fixed(Instant.parse("2022-01-01T00:00:00Z"), ZoneId.of("UTC"));
        ModuleRecordMapper moduleRecordMapper = new ModuleRecordMapperImpl();
        moduleRecordMapper.setClock(clock);

        return new ModuleRecordService(
                moduleRecordRepository,
                moduleRecordMapper,
                new PatchHelper(),
                courseRecordRepository
        );
    }

    @Test
    @SneakyThrows
    public void testBookEvent() {
        ModuleRecord record = getModuleRecord();
        ModuleRecordService service = getService();
        String json = "[" +
                "{ \"op\": \"replace\", \"path\": \"/bookingStatus\", \"value\": \"REQUESTED\" }," +
                "{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"REGISTERED\" }," +
                "{ \"op\": \"replace\", \"path\": \"/updatedAt\", \"value\": \"2022-01-01T00:00:00\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventDate\", \"value\": \"2022-01-01T00:00:00\" }," +
                "{ \"op\": \"replace\", \"path\": \"/eventId\", \"value\": \"eventID\" }" +
                "\n]";
        JsonPatch patch = JsonPatch.fromJson(mapper.readTree(json));
        Optional<ModuleRecord> mrOptional = Optional.of(record);
        Mockito.when(moduleRecordRepository.findById(1000L)).thenReturn(mrOptional);

        service.updateModuleRecord(1000L, patch);

        verify(moduleRecordRepository).save(moduleRecordArgumentCaptor.capture());

        ModuleRecord updatedRecord = moduleRecordArgumentCaptor.getValue();

        Assert.assertEquals(State.REGISTERED, updatedRecord.getState());
        Assert.assertEquals(BookingStatus.REQUESTED, updatedRecord.getBookingStatus());
        Assert.assertEquals("eventID", updatedRecord.getEventId());
        LocalDateTime updatedAt = updatedRecord.getUpdatedAt();
        Assert.assertEquals(1, updatedAt.getMonth().getValue());
        Assert.assertEquals(2022, updatedAt.getYear());
        Assert.assertEquals(1, updatedAt.getDayOfMonth());
        LocalDateTime eventDate = updatedRecord.getUpdatedAt();
        Assert.assertEquals(1, eventDate.getMonth().getValue());
        Assert.assertEquals(2022, eventDate.getYear());
        Assert.assertEquals(1, eventDate.getDayOfMonth());

    }

    @Test
    @SneakyThrows
    public void testCompleteModuleRecord() {
        ModuleRecord record = getModuleRecord();
        ModuleRecordService service = getService();
        String json = "[" +
                "{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }," +
                "{ \"op\": \"replace\", \"path\": \"/updatedAt\", \"value\": \"2022-01-01T00:00:00\" }," +
                "{ \"op\": \"replace\", \"path\": \"/completionDate\", \"value\": \"2022-01-01T00:00:00\" }" +
                "\n]";
        JsonPatch patch = JsonPatch.fromJson(mapper.readTree(json));
        Optional<ModuleRecord> mrOptional = Optional.of(record);
        Mockito.when(moduleRecordRepository.findById(1000L)).thenReturn(mrOptional);

        service.updateModuleRecord(1000L, patch);

        verify(moduleRecordRepository).save(moduleRecordArgumentCaptor.capture());

        ModuleRecord updatedRecord = moduleRecordArgumentCaptor.getValue();

        Assert.assertEquals(State.COMPLETED, updatedRecord.getState());
        LocalDateTime updatedAt = updatedRecord.getUpdatedAt();
        Assert.assertEquals(1, updatedAt.getMonth().getValue());
        Assert.assertEquals(2022, updatedAt.getYear());
        Assert.assertEquals(1, updatedAt.getDayOfMonth());
        LocalDateTime completionDate = updatedRecord.getCompletionDate();
        Assert.assertEquals(1, completionDate.getMonth().getValue());
        Assert.assertEquals(2022, completionDate.getYear());
        Assert.assertEquals(1, completionDate.getDayOfMonth());
    }

    @Test
    public void testCreateModuleRecord() {
        PostModuleRecordInput input = getModuleRecordInput();
        ModuleRecordService service = getService();
        Mockito.when(courseRecordRepository.getCourseRecord("userID", "courseID")).thenReturn(Optional.of(getCourseRecord()));
        Mockito.when(courseRecordRepository.save(any(CourseRecord.class))).thenReturn(getCourseRecord());

        service.createModuleRecord(input);

        verify(courseRecordRepository).save(courseRecordArgumentCaptor.capture());
        CourseRecord updatedRecord = courseRecordArgumentCaptor.getValue();

        ModuleRecord createdRecord = updatedRecord.getModuleRecord("moduleID");

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
