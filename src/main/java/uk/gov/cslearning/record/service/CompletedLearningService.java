package uk.gov.cslearning.record.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.dto.factory.MessageDtoFactory;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.util.HashMap;
import java.util.Map;

@Service
public class CompletedLearningService {
    private final IdentityService identityService;
    private final RegistryService registryService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @Autowired
    public CompletedLearningService(IdentityService identityService, RegistryService registryService, NotificationService notificationService, MessageService messageService) {
        this.identityService = identityService;
        this.registryService = registryService;
        this.notificationService = notificationService;
        this.messageService = messageService;
    }

    public void sendLineManagerMessage(MessageDtoFactory messageDtoFactory, String userId, String courseTitle) {
        notificationService.send(this.createLineManagerMessage(messageDtoFactory, userId, courseTitle));
    }

    public MessageDto createLineManagerMessage(MessageDtoFactory messageDtoFactory, String userId, String courseTitle) {
        Map<String, String> map = createGenericMapForLineManager(userId, courseTitle);

        System.out.println("map: " + map);
        return messageDtoFactory.create("course-manager@domain.com", "3f50f0eb-4c47-4e37-8a44-1628faa41924", map);
    }

    private Map<String, String> createGenericMapForLineManager(String userId, String courseTitle) {
        Map<String, String> map = new HashMap<>();

        registryService.getCivilServantByUid(userId).ifPresent(civilServant -> {
            System.out.println("getCivilServantByUid IS PRESENT");
            map.put("manager", civilServant.getLineManagerEmailAddress());
            map.put("learner", civilServant.getFullName());
            map.put("courseTitle", courseTitle);
        });

        return map;
    }
}
