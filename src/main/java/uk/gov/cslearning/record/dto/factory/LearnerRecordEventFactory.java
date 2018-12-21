package uk.gov.cslearning.record.dto.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.dto.LearnerRecordEvent;
import uk.gov.cslearning.record.exception.UserNotFoundException;
import uk.gov.cslearning.record.service.identity.IdentityService;

@Component
public class LearnerRecordEventFactory {

    private final IdentityService identityService;
    private final RegistryService registryService;

    public LearnerRecordEventFactory(IdentityService identityService, RegistryService registryService) {
        this.identityService = identityService;
        this.registryService = registryService;
    }

    public LearnerRecordEvent create(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        CivilServant civilServant = registryService.getCivilServantByUid(courseRecord.getUserId())
                .orElseThrow(() -> new UserNotFoundException(courseRecord.getUserId()));

        String emailAddress = identityService.getEmailAddress(courseRecord.getUserId());

        LearnerRecordEvent learnerRecordEvent = new LearnerRecordEvent();
        learnerRecordEvent.setBookingReference(String.format("REF-%s", StringUtils.leftPad(moduleRecord.getId().toString(), 6, '0')));
        learnerRecordEvent.setCourseName(courseRecord.getCourseTitle());
        learnerRecordEvent.setCourseId(courseRecord.getCourseId());
        learnerRecordEvent.setDelegateEmailAddress(emailAddress);
        learnerRecordEvent.setDelegateName(civilServant.getFullName());
        learnerRecordEvent.setDelegateUid(courseRecord.getUserId());

        learnerRecordEvent.setModuleId(moduleRecord.getModuleId());
        learnerRecordEvent.setEventId(moduleRecord.getEventId());
        learnerRecordEvent.setModuleName(moduleRecord.getModuleTitle());
        learnerRecordEvent.setCost(moduleRecord.getCost());
        learnerRecordEvent.setDate(moduleRecord.getEventDate());
        learnerRecordEvent.setCreatedAt(moduleRecord.getCreatedAt());
        learnerRecordEvent.setUpdatedAt(moduleRecord.getUpdatedAt());
        learnerRecordEvent.setPaymentMethod(moduleRecord.getPaymentMethod());
        learnerRecordEvent.setPaymentDetails(moduleRecord.getPaymentDetails());
        learnerRecordEvent.setStatus(moduleRecord.getBookingStatus());
        learnerRecordEvent.setState(moduleRecord.getState());

        return learnerRecordEvent;
    }
}
