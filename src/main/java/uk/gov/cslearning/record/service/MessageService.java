package uk.gov.cslearning.record.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.InviteDto;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageService {

    private final LearningCatalogueService learningCatalogueService;
    private final MessageDtoFactory messageDtoFactory;

    private final String bookingUrlFormat;
    private final String messageTemplateId;

    public MessageService(LearningCatalogueService learningCatalogueService,
                          MessageDtoFactory messageDtoFactory,
                          @Value("${govNotify.template.inviteLearner}") String messageTemplateId,
                          @Value("${lpg-ui.bookingUrlFormat}") String bookingUrlFormat
    ){
        this.learningCatalogueService = learningCatalogueService;
        this.messageDtoFactory = messageDtoFactory;

        this.bookingUrlFormat = bookingUrlFormat;
        this.messageTemplateId = messageTemplateId;
    }

    public MessageDto createInviteMessage(InviteDto inviteDto){
        String[] parts = inviteDto.getEvent().getPath().split("/");
        String courseId = parts[parts.length - 5];
        String moduleId = parts[parts.length - 3];

        Course course = learningCatalogueService.getCourse(courseId);
        Event event = learningCatalogueService.getEvent(inviteDto.getEvent().toString());

        Map<String, String> map = new HashMap<>();
        map.put("learnerName", inviteDto.getLearnerEmail());
        map.put("courseTitle", course.getTitle());
        map.put("courseDate", event.getDateRanges().get(0).getDate().toString());
        map.put("courseLocation", event.getVenue().getLocation());
        map.put("inviteLink", String.format(bookingUrlFormat, courseId, moduleId));

        return messageDtoFactory.create(inviteDto.getLearnerEmail(), messageTemplateId, map);
    }
}
