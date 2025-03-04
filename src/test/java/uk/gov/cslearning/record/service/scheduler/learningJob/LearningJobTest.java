package uk.gov.cslearning.record.service.scheduler.learningJob;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.Notification;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.repository.NotificationRepository;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.scheduler.LearningJob;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("learning-job")
public class LearningJobTest extends IntegrationTestBase {
    String coCivilServant1 = "coCivilServant1";
    String coCivilServant2 = "coCivilServant2";
    String dwpCivilServant1 = "dwpCivilServant1";
    String dwpCivilServant2 = "dwpCivilServant2";
    String hmrcCivilServant1 = "hmrcCivilServant1";
    String hmrcCivilServant2 = "hmrcCivilServant2";
    LocalDateTime completedDate = LocalDateTime.of(2022, 12, 1, 10, 0, 0);
    LocalDateTime incompleteDate = LocalDateTime.of(2021, 12, 1, 10, 0, 0);
    @Autowired
    private TestDataService testDataService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LearningJob learningJob;
    @Autowired
    private CourseRecordRepository courseRecordRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    private Map<String, List<Course>> generateRequiredCourses() {
        Course course1 = testDataService.generateCourse("course1", "Course 1");
        course1.setModules(List.of(
                testDataService.generateModule("module1", true),
                testDataService.generateModule("module2", false),
                testDataService.generateModule("module3", false)
        ));
        course1.setAudiences(List.of(
                testDataService.generateRequiredLearningAudience(List.of("CO", "HMRC"),
                        LocalDate.of(2022, 1, 6), "P1Y"),
                testDataService.generateRequiredLearningAudience(List.of("DWP"),
                        LocalDate.of(2023, 1, 2), "P1Y")
        ));

        Course course2 = testDataService.generateCourse("course2", "Course 2");
        course2.setModules(List.of(
                testDataService.generateModule("module4", false),
                testDataService.generateModule("module5", false)
        ));
        course2.setAudiences(List.of(
                testDataService.generateRequiredLearningAudience(List.of("DWP"),
                        LocalDate.of(2023, 1, 6), "P1Y"),
                testDataService.generateRequiredLearningAudience(List.of("HMRC"),
                        LocalDate.of(2023, 1, 2), "P1Y")
        ));

        Course course3 = testDataService.generateCourse("course3", "Course 3");
        course3.setModules(List.of(
                testDataService.generateModule("module6", false)
        ));
        course3.setAudiences(List.of(
                testDataService.generateRequiredLearningAudience(List.of("CO"),
                        LocalDate.of(2022, 1, 6), "P1Y")
        ));
        return Map.of(
                "CO", List.of(course1, course3),
                "DWP", List.of(course1, course2),
                "HMRC", List.of(course1, course2));
    }

    /**
     * "today": 2023-01-01
     * --
     * CO: course1 and course3 are due in 5 days
     * DWP: course1 is due in 1 day, course2 is due in 5 days
     * HMRC: course1 is due in 5 days, course2 is due in 1 day
     * --
     * CO: coCivilServant1, coCivilServant2
     * DWP: dwpCivilServant1, dwpCivilServant2
     * HMRC: hmrcCivilServant1, hmrcCivilServant2
     *
     * @throws IOException
     */
    public void loadCoursesAndLearners() throws IOException {

        stubService.getIdentityServiceStubService().getClientToken();
        String requiredLearningResponse = objectMapper.writeValueAsString(generateRequiredCourses());
        stubService.getLearningCatalogueStubService().getRequiredCoursesByDueDaysGroupedByOrg("1,5", requiredLearningResponse);

        Map.of("CO", """
                                [
                                  {
                                    "identity": {
                                      "uid": "coCivilServant1"
                                    }
                                  },
                                  {
                                    "identity": {
                                      "uid": "coCivilServant2"
                                    }
                                  }
                                ]
                                """,
                        "DWP", """
                                [
                                  {
                                    "identity": {
                                      "uid": "dwpCivilServant1"
                                    }
                                  },
                                  {
                                    "identity": {
                                      "uid": "dwpCivilServant2"
                                    }
                                  }
                                ]
                                                                
                                """,
                        "HMRC", """
                                [
                                  {
                                    "identity": {
                                      "uid": "hmrcCivilServant1"
                                    }
                                  },
                                  {
                                    "identity": {
                                      "uid": "hmrcCivilServant2"
                                    }
                                  }
                                ]
                                """)
                .forEach((depCode, json) -> stubService.getCsrsStubService().getCivilServantsForDepartment(depCode, json));
    }

    private CourseRecord createCourseRecordForUser(String uid, String courseId, Map<String, LocalDateTime> moduleRecords) {
        CourseRecord courseRecord = new CourseRecord(courseId, uid);
        courseRecord.setCourseTitle(courseId);
        createModuleRecords(moduleRecords).forEach(courseRecord::addModuleRecord);
        return courseRecord;
    }

    private List<ModuleRecord> createModuleRecords(Map<String, LocalDateTime> idsToCompletionDates) {
        return idsToCompletionDates.entrySet().stream()
                .map(stringLocalDateTimeEntry -> createModuleRecordWithCompletionDate(stringLocalDateTimeEntry.getKey(), stringLocalDateTimeEntry.getValue())).toList();
    }

    private ModuleRecord createModuleRecordWithCompletionDate(String moduleId, @Nullable LocalDateTime completionDate) {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setModuleId(moduleId);
        moduleRecord.setModuleType("link");
        moduleRecord.setOptional(false);
        moduleRecord.setModuleTitle(moduleId);
        moduleRecord.setCompletionDate(completionDate);
        if (completionDate != null) {
            moduleRecord.setState(State.COMPLETED);
        } else {
            moduleRecord.setState(State.IN_PROGRESS);
        }
        return moduleRecord;
    }

    private CourseRecord createCourseRecordsForCourse1(String uid, LocalDateTime module1CompletionDate,
                                                       LocalDateTime module2CompletionDate, LocalDateTime module3CompletionDate) {
        return createCourseRecordForUser(uid, "course1", Map.of(
                "module1", module1CompletionDate,
                "module2", module2CompletionDate,
                "module3", module3CompletionDate
        ));
    }

    private CourseRecord createCourseRecordsForCourse2(String uid, LocalDateTime module1CompletionDate,
                                                       LocalDateTime module2CompletionDate) {
        return createCourseRecordForUser(uid, "course2", Map.of(
                "module4", module1CompletionDate,
                "module5", module2CompletionDate
        ));
    }

    private CourseRecord createCourseRecordsForCourse3(String uid, LocalDateTime module1CompletionDate) {
        return createCourseRecordForUser(uid, "course3", Map.of(
                "module6", module1CompletionDate
        ));
    }

    private void createCourseRecords() {
        List<CourseRecord> totalRecords = new ArrayList<>();
        // CO
        // coCivilServant1 has completed all of course 1 but has not completed course 3
        // coCivilServant2 has not completed course 1 or 3
        totalRecords.addAll(List.of(
                createCourseRecordsForCourse1(coCivilServant1, completedDate, completedDate, completedDate),
                createCourseRecordsForCourse3(coCivilServant1, incompleteDate)
        ));
        totalRecords.addAll(List.of(
                createCourseRecordsForCourse1(coCivilServant2, completedDate, incompleteDate, completedDate),
                createCourseRecordsForCourse3(coCivilServant2, incompleteDate)
        ));

        // DWP
        // dwpCivilServant1 has completed all of course 1 and all of course 2
        // dwpCivilServant2 has never started course 1 and has not completed course 2
        totalRecords.addAll(List.of(
                createCourseRecordsForCourse1(dwpCivilServant1, completedDate, completedDate, completedDate),
                createCourseRecordsForCourse2(dwpCivilServant1, completedDate, completedDate)
        ));
        totalRecords.add(
                createCourseRecordsForCourse2(dwpCivilServant2, incompleteDate, incompleteDate)
        );

        // HMRC
        // hmrcCivilServant1 has not started any courses
        // hmrcCivilServant2 has completed all courses
        totalRecords.addAll(List.of(
                createCourseRecordsForCourse1(hmrcCivilServant2, completedDate, completedDate, completedDate),
                createCourseRecordsForCourse2(hmrcCivilServant2, completedDate, completedDate)
        ));
        courseRecordRepository.saveAllAndFlush(totalRecords);
    }

    private void mockIncompleteLearningEmail(String recipient, String courseTitles, String periodText) {
        stubService.getNotificationServiceStubService().sendEmail("REQUIRED_LEARNING_DUE",
                String.format("""
                        {
                            "recipient": "%s",
                            "personalisation": {
                                "email address": "%s",
                                "periodPermission": "%s",
                                "requiredLearning": "%s"
                            },
                            "reference": "UUID"
                        }
                        """, recipient, recipient, periodText, courseTitles));
    }

    @Test
    public void testLearningJob() throws IOException {
        loadCoursesAndLearners();
        createCourseRecords();
        /*
        Should send:
        - 1 email to coCivilServant1 for course 3 (5 days deadline)
        - 1 email to coCivilServant2 for course 1 and 3 (5 days deadline)
        - 2 emails to dwpCivilServant2 (course 1 (1 day deadline), course 2 (5 days deadline))
        - 2 emails to hmrcCivilServant1 (course 1 (5 day deadline), course 2 (1 day deadline))
         */
        // 1 day deadline email batch
        stubService.getIdentityServiceStubService().getIdentitiesMap(List.of("hmrcCivilServant1", "dwpCivilServant2"),
                """
                        {
                            "hmrcCivilServant1": {
                                "username": "hmrcCivilServant1@hmrc.gov.uk",
                                "uid": "hmrcCivilServant1"
                            },
                            "dwpCivilServant2": {
                                "username": "dwpCivilServant2@dwp.gov.uk",
                                "uid": "dwpCivilServant2"
                            }
                        }
                        """);
        // 5 day deadline email batch
        stubService.getIdentityServiceStubService().getIdentitiesMap(List.of("coCivilServant1", "coCivilServant2"),
                """
                        {
                            "coCivilServant1": {
                                "username": "coCivilServant1@cabinetoffice.gov.uk",
                                "uid": "coCivilServant1"
                            },
                            "coCivilServant2": {
                                "username": "coCivilServant2@cabinetoffice.gov.uk",
                                "uid": "coCivilServant2"
                            }
                        }
                        """);
        mockIncompleteLearningEmail("hmrcCivilServant1@hmrc.gov.uk", "Course 1\\n", "5 days");
        mockIncompleteLearningEmail("hmrcCivilServant1@hmrc.gov.uk", "Course 2\\n", "1 day");

        mockIncompleteLearningEmail("dwpCivilServant2@dwp.gov.uk", "Course 1\\n", "1 day");
        mockIncompleteLearningEmail("dwpCivilServant2@dwp.gov.uk", "Course 2\\n", "5 days");

        mockIncompleteLearningEmail("coCivilServant2@cabinetoffice.gov.uk", "Course 3\\nCourse 1\\n", "5 days");
        mockIncompleteLearningEmail("coCivilServant1@cabinetoffice.gov.uk", "Course 3\\n", "5 days");

        learningJob.sendReminderNotificationForIncompleteCourses();
        List<Notification> notifications = new ArrayList<>((Collection<Notification>) notificationRepository.findAll());
        assertEquals(7, notifications.size());
    }
}
