package uk.gov.cslearning.record.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRecordServiceTest {

    private UserRecordService userRecordService;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @Mock
    private RegistryService registryService;

    @Mock
    private CourseRecordRepository courseRecordRepository;

    @Mock
    private CollectionsService collectionsService;

    @Before
    public void setup() {
        userRecordService = new UserRecordService(courseRecordRepository,
                learningCatalogueService, collectionsService, registryService);
    }

    @Test
    public void shouldDeleteUserRecords() throws Exception {
        String uid = "userId";

        userRecordService.deleteUserRecords(uid);

        verify(collectionsService).deleteAllByLearnerUid(uid);
        verify(courseRecordRepository).deleteAllByUid(uid);
    }

    @Test
    public void shouldDeleteStatementsOlderThanDateTime() {
        DateTime dateTime = DateTime.now().minusMonths(36);
        Instant instant = DateTimeFormatter.ISO_DATE_TIME.parse(dateTime.toString(), Instant::from);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        userRecordService.deleteOldRecords(dateTime, localDateTime);

        verify(collectionsService).deleteAllByAge(any());
    }

}
