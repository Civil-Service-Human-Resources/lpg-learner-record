package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.config.LpgUiConfig;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.EventIds;
import uk.gov.cslearning.record.dto.CancellationReason;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.exception.CivilServantNotFoundException;
import uk.gov.cslearning.record.notifications.dto.*;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.Module;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private final RegistryService registryService;
    private final LearningCatalogueService learningCatalogueService;
    private final LpgUiConfig lpgUiConfig;

    public MessageService(RegistryService registryService, LearningCatalogueService learningCatalogueService,
                          LpgUiConfig lpgUiConfig) {
        this.registryService = registryService;
        this.learningCatalogueService = learningCatalogueService;
        this.lpgUiConfig = lpgUiConfig;
    }

    public IMessageParams createIncompleteCoursesMessage(String email, List<String> requiredLearningTitles, String period) {
        StringBuilder requiredLearningStr = new StringBuilder();
        for (String title : requiredLearningTitles) {
            requiredLearningStr
                    .append(title)
                    .append("\n");
        }
        return new RequiredLearningDueMessageParams(email, period, requiredLearningStr.toString());
    }

    public IMessageParams createInviteMessage(InviteDto inviteDto) {
        EventIds eventIds = inviteDto.getEventIds();
        CourseMessageDetails courseMessageDetails = getCourseMessageDetails(eventIds);
        return new InviteLearnerToEventMessageParams(
                inviteDto.getLearnerEmail(),
                courseMessageDetails,
                lpgUiConfig.getBookingUrl(eventIds.getCourseId(), eventIds.getModuleId())
        );
    }

    public List<IMessageParams> createCancelBookingMessages(Booking booking) {
        CivilServant civilServant = getCivilServant(booking.getLearner().getUid());
        LearnerMessageDetails learnerMessageDetails = new LearnerMessageDetails(civilServant.getFullName(), booking.getLearner().getLearnerEmail());
        CourseMessageDetails courseMessageDetails = getCourseMessageDetails(booking.getEvent().getEventIds());
        IMessageParams learnerParams = CancelBookingMessageParams.createFromBooking(booking, courseMessageDetails);
        IMessageParams lmParams = new CancelBookingLMMessageParams(civilServant.getLineManagerEmailAddress(), courseMessageDetails,
                learnerMessageDetails, booking.getBookingReference());
        return List.of(learnerParams, lmParams);
    }

    public List<IMessageParams> createBulkCancelEventMessages(uk.gov.cslearning.record.domain.Event event, CancellationReason cancellationReason) {
        CourseMessageDetails courseMessageDetails = getCourseMessageDetails(event.getEventIds());
        List<IMessageParams> params = new ArrayList<>();
        event.getBookings().forEach(b -> params.add(new CancelEventMessageParams(b.getLearner().getLearnerEmail(), courseMessageDetails, cancellationReason.getValue(), b.getBookingReference())));
        return params;
    }

    public List<IMessageParams> createRegisteredMessages(Booking booking) {
        CivilServant civilServant = getCivilServant(booking.getLearner().getUid());
        LearnerMessageDetails learnerMessageDetails = new LearnerMessageDetails(civilServant.getFullName(), booking.getLearner().getLearnerEmail());
        CourseMessageDetails courseMessageDetails = getCourseMessageDetails(booking.getEvent().getEventIds());
        IMessageParams learnerParams = RequestBookingMessageParams.createFromBooking(booking, courseMessageDetails);
        IMessageParams lmParams = new RequestBookingLMMessageParams(civilServant.getLineManagerEmailAddress(), learnerMessageDetails, courseMessageDetails,
                booking.getBookingReference());
        return List.of(learnerParams, lmParams);
    }

    public List<IMessageParams> createBookedMessages(Booking booking) {
        CivilServant civilServant = getCivilServant(booking.getLearner().getUid());
        LearnerMessageDetails learnerMessageDetails = new LearnerMessageDetails(civilServant.getFullName(), booking.getLearner().getLearnerEmail());
        CourseMessageDetails courseMessageDetails = getCourseMessageDetails(booking.getEvent().getEventIds());
        IMessageParams learnerParams = ConfirmBookingMessageParams.createFromBooking(booking, courseMessageDetails);
        IMessageParams lmParams = new ConfirmBookingLMMessageParams(civilServant.getLineManagerEmailAddress(), learnerMessageDetails, courseMessageDetails,
                booking.getBookingReference());
        return List.of(learnerParams, lmParams);
    }

    private CourseMessageDetails getCourseMessageDetails(EventIds eventIds) {
        Course course = learningCatalogueService.getCourse(eventIds.getCourseId());
        Module module = course.getModule(eventIds.getModuleId());
        Event event = course.getEvent(eventIds.getEventId());
        return getCourseMessageDetails(course, module, event);
    }

    private CourseMessageDetails getCourseMessageDetails(Course course, Module module, Event event) {
        return new CourseMessageDetails(course.getTitle(), event.getFirstDateAsString(),
                event.getVenue().getLocation(), module.getCost().toString());
    }

    private CivilServant getCivilServant(String learnerUid) {
        return registryService.getCivilServantResourceByUid(learnerUid).orElseThrow(() -> new CivilServantNotFoundException(learnerUid));
    }

}
