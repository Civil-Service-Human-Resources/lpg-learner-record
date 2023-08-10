package uk.gov.cslearning.record.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapper;
import uk.gov.cslearning.record.api.mapper.ModuleRecordMapperImpl;
import uk.gov.cslearning.record.api.util.PatchHelper;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.ModuleRecordRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static uk.gov.cslearning.record.TestUtils.assertDate;
import static uk.gov.cslearning.record.TestUtils.assertTime;

@RunWith(MockitoJUnitRunner.class)
public class ModuleRecordServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private ModuleRecordRepository moduleRecordRepository;

    @Captor
    private ArgumentCaptor<ModuleRecord> moduleRecordArgumentCaptor;

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
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00Z"), ZoneId.of("UTC"));
        ModuleRecordMapper moduleRecordMapper = new ModuleRecordMapperImpl();
        moduleRecordMapper.setClock(clock);

        return new ModuleRecordService(
                moduleRecordRepository,
                moduleRecordMapper,
                new PatchHelper(),
                courseRecordRepository,
                clock
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
                "{ \"op\": \"replace\", \"path\": \"/eventDate\", \"value\": \"2023-02-02T00:00:00\" }," +
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
        assertTime(updatedRecord.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertDate(updatedRecord.getEventDate(), 2, 2, 2023);
    }

    @Test
    @SneakyThrows
    public void testCompleteModuleRecord() {
        ModuleRecord record = getModuleRecord();
        ModuleRecordService service = getService();
        String json = "[" +
                "{ \"op\": \"replace\", \"path\": \"/state\", \"value\": \"COMPLETED\" }," +
                "{ \"op\": \"replace\", \"path\": \"/completionDate\", \"value\": \"2023-02-02T10:00:00\" }" +
                "\n]";
        JsonPatch patch = JsonPatch.fromJson(mapper.readTree(json));
        Optional<ModuleRecord> mrOptional = Optional.of(record);
        Mockito.when(moduleRecordRepository.findById(1000L)).thenReturn(mrOptional);

        service.updateModuleRecord(1000L, patch);

        verify(moduleRecordRepository).save(moduleRecordArgumentCaptor.capture());

        ModuleRecord updatedRecord = moduleRecordArgumentCaptor.getValue();

        Assert.assertEquals(State.COMPLETED, updatedRecord.getState());
        assertTime(updatedRecord.getUpdatedAt(), 1, 1, 2023, 10, 0, 0);
        assertTime(updatedRecord.getCompletionDate(), 2, 2, 2023, 10, 0, 0);
    }

}
