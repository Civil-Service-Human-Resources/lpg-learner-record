package uk.gov.cslearning.record.service.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.identity.CustomHttpService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompletedLearningServiceTest {

    @Mock
    private CustomHttpService customHttpService;

    @Mock
    private UserRecordService userRecordService;

    @Mock
    private ScheduledNotificationsService scheduledNotificationsService;

    @InjectMocks
    private CompletedLearningService completedLearningService;

    @Test
    public void shouldSendLineManagerNotificationForCompletedLearning() {
        String code1 = "code1";
        String code2 = "code2";
        String uid1 = "8c9aba18-b351-461a-a117-e650d07bbc5c";
        String uid2 = "uid2";
        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";
        String id4 = "id4";
        String name = "name";
        String username = "lm@example.com";

        CivilServantDto civilServantDto1 = new CivilServantDto();
        civilServantDto1.setOrganisation(code1);
        civilServantDto1.setUid(uid1);
        civilServantDto1.setName(name);
        civilServantDto1.setLineManagerUid(uid2);

        Map<String, CivilServantDto> civilServantMap = new HashMap<>();
        civilServantMap.put(civilServantDto1.getUid(), civilServantDto1);

        IdentityDTO identityDTO = new IdentityDTO();
        identityDTO.setUid(uid1);

        IdentityDTO identityDTO2 = new IdentityDTO();
        identityDTO2.setUid(uid2);
        identityDTO2.setUsername(username);

        Map<String, IdentityDTO> identityDTOMap = new HashMap<>();
        identityDTOMap.put(uid1, identityDTO);
        identityDTOMap.put(uid2, identityDTO2);

        Course course1 = new Course();
        course1.setId(id1);
        Course course2 = new Course();
        course2.setId(id2);

        List<Course> code1Courses = Arrays.asList(course1, course2);
        Map<String, List<Course>> organisationalUnitRequiredLearningMap = new HashMap<>();
        organisationalUnitRequiredLearningMap.put(code1, code1Courses);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String completed = "2019-02-01 11:30";
        LocalDateTime completedDate = LocalDateTime.parse(completed, formatter);

        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setCompletionDate(completedDate);

        CourseRecord courseRecord = new CourseRecord(id1, uid1);
        courseRecord.setState(State.COMPLETED);
        courseRecord.setModuleRecords(Arrays.asList(moduleRecord));
        List<CourseRecord> courseRecords = Arrays.asList(courseRecord);

        when(customHttpService.getCivilServantMap()).thenReturn(civilServantMap);
        when(customHttpService.getIdentitiesMap()).thenReturn(identityDTOMap);
        when(customHttpService.getOrganisationalUnitRequiredLearning()).thenReturn(organisationalUnitRequiredLearningMap);
        when(userRecordService.getStoredUserRecord(uid1, Arrays.asList(id1, id2))).thenReturn(courseRecords);

        when(scheduledNotificationsService.hasNotificationBeenSentBefore(uid1, id1, completedDate)).thenReturn(false);

        completedLearningService.sendLineManagerNotificationForCompletedLearning();

        verify(scheduledNotificationsService).sendNotification(username, name, uid1, courseRecord);
    }
}