package uk.gov.cslearning.record;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.*;
import uk.gov.cslearning.record.domain.record.LearnerRecord;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.EventStatus;
import uk.gov.cslearning.record.service.catalogue.Audience;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Module;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
@Data
public class TestDataService {

    protected final String courseId = "testCourseId";
    protected final String courseTitle = "Test title";
    protected final String moduleId = "testModuleId";
    protected final String eventId = "testEventId";
    protected final String userId = "testUserId";
    @Autowired
    private IUtilService utilService;

    public Learner generateLearner() {
        String learnerId = generateLearnerId();
        return new Learner(learnerId, String.format("%s@email.com", learnerId));
    }

    public String generateLearnerId() {
        return String.format("learner-%s", generateRandomID());
    }

    public String generateRandomID() {
        return RandomStringUtils.random(5, true, true);
    }

    public Booking generateBooking(BookingStatus status, Learner learner) {
        Instant now = utilService.getNowInstant();
        Booking booking = new Booking();
        booking.setLearner(learner);
        booking.setStatus(status);
        booking.setBookingTime(now);
        booking.setBookingReference("ABCDE");
        if (status.equals(BookingStatus.CONFIRMED)) {
            booking.setConfirmationTime(now);
        } else if (status.equals(BookingStatus.CANCELLED)) {
            booking.setCancellationReason(BookingCancellationReason.ILLNESS);
            booking.setConfirmationTime(now);
        }
        return booking;
    }

    public Event generateLearnerRecordEvent() {
        Event event = new Event();
        event.setStatus(EventStatus.ACTIVE);
        event.setPath("/courses/courseId/modules/moduleId/events/" + eventId);
        event.setUid(eventId);
        return event;
    }

    public ModuleRecord generateModuleRecord() {
        ModuleRecord mr = new ModuleRecord();
        mr.setCourseId(courseId);
        mr.setCourseTitle(courseTitle);
        mr.setUserId(userId);
        mr.setModuleId(moduleId);
        mr.setDuration(100L);
        mr.setState(State.IN_PROGRESS);
        mr.setModuleTitle("Test module title");
        mr.setModuleType("elearning");
        mr.setOptional(false);
        return mr;
    }

    public CourseRecord generateCourseRecord(int numberOfModuleRecords) {
        CourseRecord cr = new CourseRecord(courseId, userId);
        cr.setCourseTitle("Test title");
        cr.setRequired(true);
        for (int i = 0; i < numberOfModuleRecords; i++) {
            ModuleRecord mr = generateModuleRecord();
            mr.setModuleId(moduleId + i);
            mr.setDuration(100L);
            mr.setModuleTitle("Test module title");
            mr.setState(State.IN_PROGRESS);
            mr.setModuleType("elearning");
            mr.setCourseRecord(cr);
            cr.addModuleRecord(mr);
        }
        return cr;
    }

    public Course generateCourse(String id, String title) {
        return new Course(id, title, List.of(), List.of());
    }

    public Module generateModule(String id, boolean optional) {
        Module module = new Module();
        module.setId(id);
        module.setOptional(optional);
        return module;
    }

    public Audience generateRequiredLearningAudience(List<String> departments, LocalDate requiredBy, String frequency) {
        Audience audience = new Audience();
        audience.setDepartments(departments);
        audience.setRequiredBy(requiredBy);
        audience.setFrequency(frequency);
        return audience;
    }

    // New records

    public LearnerRecord createCourseRecord() {
        return new LearnerRecord();
    }
}
