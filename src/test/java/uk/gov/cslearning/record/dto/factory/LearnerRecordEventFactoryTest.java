package uk.gov.cslearning.record.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.dto.LearnerRecordEvent;
import uk.gov.cslearning.record.exception.UserNotFoundException;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LearnerRecordEventFactoryTest {

    @Mock
    private IdentityService identityService;

    @Mock
    private RegistryService registryService;

    @InjectMocks
    private LearnerRecordEventFactory learnerRecordEventFactory;

    @Test
    public void shouldReturnLearnerRecordEvent() {
        String courseId = "course-id";
        String userId = "user-id";
        String email = "user@example.org";
        String moduleId = "module-id";
        long moduleRecordId = 837239;
        String courseName = "course-name";
        String delegateName = "delegate-name";
        String eventId = "event-id";
        String moduleName = "module-name";
        BigDecimal cost = new BigDecimal("99.99");
        LocalDate date = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(12);
        String paymentMethod = "payment-method";
        String paymentDetails = "payment-details";
        BookingStatus bookingStatus = BookingStatus.CONFIRMED;
        State state = State.APPROVED;

        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        courseRecord.setCourseTitle(courseName);

        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setId(moduleRecordId);
        moduleRecord.setEventId(eventId);
        moduleRecord.setModuleTitle(moduleName);
        moduleRecord.setCost(cost);
        moduleRecord.setEventDate(date);
        moduleRecord.setCreatedAt(createdAt);
        moduleRecord.setUpdatedAt(updatedAt);
        moduleRecord.setPaymentMethod(paymentMethod);
        moduleRecord.setPaymentDetails(paymentDetails);
        moduleRecord.setBookingStatus(bookingStatus);
        moduleRecord.setState(state);

        CivilServant civilServant = new CivilServant();
        civilServant.setFullName(delegateName);

        when(registryService.getCivilServantByUid(userId)).thenReturn(Optional.of(civilServant));
        when(identityService.getEmailAddress(userId)).thenReturn(email);

        LearnerRecordEvent learnerRecordEvent = learnerRecordEventFactory.create(courseRecord, moduleRecord);
        assertEquals(courseId, learnerRecordEvent.getCourseId());
        assertEquals(userId, learnerRecordEvent.getDelegateUid());
        assertEquals(email, learnerRecordEvent.getDelegateEmailAddress());
        assertEquals(moduleId, learnerRecordEvent.getModuleId());
        assertEquals(courseName, learnerRecordEvent.getCourseName());
        assertEquals(delegateName, learnerRecordEvent.getDelegateName());
        assertEquals(eventId, learnerRecordEvent.getEventId());
        assertEquals(moduleName, learnerRecordEvent.getModuleName());
        assertEquals(cost, learnerRecordEvent.getCost());
        assertEquals(date, learnerRecordEvent.getDate());
        assertEquals(createdAt, learnerRecordEvent.getCreatedAt());
        assertEquals(updatedAt, learnerRecordEvent.getUpdatedAt());
        assertEquals(paymentMethod, learnerRecordEvent.getPaymentMethod());
        assertEquals(paymentDetails, learnerRecordEvent.getPaymentDetails());
        assertEquals(bookingStatus, learnerRecordEvent.getStatus());
        assertEquals(state, learnerRecordEvent.getState());
    }

    @Test
    public void shouldThrowUserNotFoundException() {
        try {
            learnerRecordEventFactory.create(new CourseRecord("", "user-id"), new ModuleRecord());
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            assertEquals("Unknown user: user-id", e.getMessage());
        }
    }
}