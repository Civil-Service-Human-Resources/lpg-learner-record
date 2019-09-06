package uk.gov.cslearning.record.service.scheduler;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.CompletedLearningEvent;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.domain.scheduler.LineManagerRequiredLearningNotificationEvent;
import uk.gov.cslearning.record.domain.scheduler.RequiredLearningDueNotificationEvent;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.CompletedLearningEventService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Module;
import uk.gov.cslearning.record.service.http.CustomHttpService;
import uk.gov.cslearning.record.service.scheduler.events.LineManagerRequiredLearningNotificationEventService;
import uk.gov.cslearning.record.service.scheduler.events.RequiredLearningDueNotificationEventService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {
    @Mock
    private CustomHttpService customHttpService;

    @Mock
    private UserRecordService userRecordService;

    @Mock
    private CompletedLearningEventService completedLearningService;

    @Mock
    private RequiredLearningDueNotificationEventService requiredLearningDueNotificationEventService;

    @Mock
    private LineManagerRequiredLearningNotificationEventService lineManagerRequiredLearningNotificationEventService;

    @InjectMocks
    private SchedulerService schedulerService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void processReminderNotificationForIncompleteLearning() {
        String username1 = "user1@example.com";
        String username2 = "user2@example.com";
        String username3 = "user3@example.com";
        String uid1 = "uid1";
        String uid2 = "uid2";
        String uid3 = "uid3";

        IdentityDto identityDto = new IdentityDto();
        identityDto.setUsername(username1);
        identityDto.setUid(uid1);

        IdentityDto identityDto2 = new IdentityDto();
        identityDto2.setUsername(username2);
        identityDto2.setUid(uid2);

        IdentityDto identityDto3 = new IdentityDto();
        identityDto3.setUsername(username3);
        identityDto3.setUid(uid3);

        Map<String, IdentityDto> identitiesMap = new HashMap<>();
        identitiesMap.put(uid1, identityDto);
        identitiesMap.put(uid2, identityDto2);
        identitiesMap.put(uid3, identityDto3);

        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";
        String title1 = "title1";
        String title2 = "title2";
        String title3 = "title3";

        Module module = new Module();
        module.setId(id1);

        Course course1 = new Course();
        course1.setId(id1);
        course1.setTitle(title1);
        course1.setModules(Collections.singletonList(module));

        Course course2 = new Course();
        course2.setId(id2);
        course2.setTitle(title2);
        course2.setModules(Collections.singletonList(module));

        Course course3 = new Course();
        course3.setId(id3);
        course3.setTitle(title3);
        course3.setModules(Collections.singletonList(module));

        String code1 = "co";
        String code2 = "hmrc";

        Map<String, List<Course>> dayRequiredCourses = new HashMap<>();
        Map<String, List<Course>> weekRequiredCourses = new HashMap<>();
        Map<String, List<Course>> monthRequiredCourses = new HashMap<>();

        dayRequiredCourses.put(code1, Arrays.asList(course1, course2));
        weekRequiredCourses.put(code2, Arrays.asList(course3));

        CivilServantDto civilServantDto1 = new CivilServantDto();
        civilServantDto1.setUid(uid1);
        CivilServantDto civilServantDto2 = new CivilServantDto();
        civilServantDto2.setUid(uid2);
        CivilServantDto civilServantDto3 = new CivilServantDto();
        civilServantDto3.setUid(uid3);

        Map<String, CivilServantDto> code1CivilServants = new HashMap<>();
        code1CivilServants.put(uid1, civilServantDto1);
        code1CivilServants.put(uid2, civilServantDto2);
        Map<String, CivilServantDto> code2CivilServants = new HashMap<>();
        code2CivilServants.put(uid3, civilServantDto3);

        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);

        CourseRecord courseRecord1 = new CourseRecord(id1, uid1);
        courseRecord1.addModuleRecord(moduleRecord);

        CourseRecord courseRecord2 = new CourseRecord(id2, uid2);
        courseRecord2.addModuleRecord(moduleRecord);

        CourseRecord courseRecord3 = new CourseRecord(id3, uid3);
        courseRecord3.addModuleRecord(moduleRecord);

        when(customHttpService.getIdentitiesMap()).thenReturn(identitiesMap);
        when(customHttpService.getRequiredLearningDueWithinPeriod(0, 1)).thenReturn(dayRequiredCourses);
        when(customHttpService.getRequiredLearningDueWithinPeriod(1, 7)).thenReturn(weekRequiredCourses);
        when(customHttpService.getRequiredLearningDueWithinPeriod(7, 30)).thenReturn(monthRequiredCourses);

        when(customHttpService.getCivilServantMapByOrganisation(code1)).thenReturn(code1CivilServants);
        when(customHttpService.getCivilServantMapByOrganisation(code2)).thenReturn(code2CivilServants);

//        when(userRecordService.getStoredUserRecord(uid1, Arrays.asList(id1))).thenReturn(Arrays.asList(courseRecord1));
//        when(userRecordService.getStoredUserRecord(uid2, Arrays.asList(id2))).thenReturn(Arrays.asList(courseRecord2));
        when(userRecordService.getStoredUserRecord(uid3, Arrays.asList(id3))).thenReturn(Arrays.asList(courseRecord3));

        schedulerService.processReminderNotificationForIncompleteLearning();

        verify(requiredLearningDueNotificationEventService, times(5)).save(any(RequiredLearningDueNotificationEvent.class));
    }

    @Test
    public void processLineManagerNotificationForCompletedLearning() {
        String username1 = "user1@example.com";
        String username2 = "user2@example.com";
        String username3 = "user3@example.com";
        String uid1 = "uid1";
        String uid2 = "uid2";
        String uid3 = "uid3";

        IdentityDto identityDto = new IdentityDto();
        identityDto.setUsername(username1);
        identityDto.setUid(uid1);

        IdentityDto identityDto2 = new IdentityDto();
        identityDto2.setUsername(username2);
        identityDto2.setUid(uid2);

        IdentityDto identityDto3 = new IdentityDto();
        identityDto3.setUsername(username3);
        identityDto3.setUid(uid3);

        Map<String, IdentityDto> identitiesMap = new HashMap<>();
        identitiesMap.put(uid1, identityDto);
        identitiesMap.put(uid2, identityDto2);
        identitiesMap.put(uid3, identityDto3);

        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";
        String title1 = "title1";
        String title2 = "title2";
        String title3 = "title3";

        Module module = new Module();
        module.setId(id1);

        Course course1 = new Course();
        course1.setId(id1);
        course1.setTitle(title1);
        course1.setModules(Collections.singletonList(module));

        Course course2 = new Course();
        course2.setId(id2);
        course2.setTitle(title2);
        course2.setModules(Collections.singletonList(module));

        String code1 = "co";

        Map<String, List<Course>> dayRequiredCourses = new HashMap<>();

        dayRequiredCourses.put(code1, Arrays.asList(course1, course2));

        CivilServantDto civilServantDto1 = new CivilServantDto();
        civilServantDto1.setUid(uid1);
        civilServantDto1.setOrganisation(code1);
        civilServantDto1.setName(username1);
        civilServantDto1.setLineManagerUid(uid3);
        CivilServantDto civilServantDto2 = new CivilServantDto();
        civilServantDto2.setUid(uid2);
        civilServantDto2.setOrganisation(code1);
        civilServantDto2.setName(username2);
        civilServantDto2.setLineManagerUid(uid3);

        Map<String, CivilServantDto> code1CivilServants = new HashMap<>();
        code1CivilServants.put(civilServantDto1.getUid(), civilServantDto1);

        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);

        CourseRecord courseRecord1 = new CourseRecord(id1, uid1);
        courseRecord1.addModuleRecord(moduleRecord);
        courseRecord1.setCourseTitle(title1);

        CompletedLearningEvent completedLearningEvent = new CompletedLearningEvent();
        completedLearningEvent.setCourseRecord(courseRecord1);

        when(customHttpService.getIdentitiesMap()).thenReturn(identitiesMap);
        when(customHttpService.getOrganisationalUnitRequiredLearning()).thenReturn(dayRequiredCourses);
        when(customHttpService.getCivilServantsByOrganisationalUnitCodeMap()).thenReturn(code1CivilServants);
        when(completedLearningService.findAll()).thenReturn(Arrays.asList(completedLearningEvent));

        schedulerService.processLineManagerNotificationForCompletedLearning();

        verify(lineManagerRequiredLearningNotificationEventService, times(1)).save(any(LineManagerRequiredLearningNotificationEvent.class));
    }

    @Test
    public void sendLineManagerNotificationForCompletedLearningRetroactive() {
    }
}