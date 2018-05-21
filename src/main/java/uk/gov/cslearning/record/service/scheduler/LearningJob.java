package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.service.RegistryService;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.identity.Identity;
import uk.gov.cslearning.record.service.identity.IdentityService;

import java.util.Collection;
import java.util.LinkedHashMap;

public class LearningJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    private IdentityService identityService;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    private RegistryService registryService;

    public LearningJob(IdentityService identityService) {
        this.identityService = identityService;
    }

    public void getIdentityListFromResponse() {

        Collection<Identity> identities = identityService.listAll();

        for (Identity identity: identities){
            LOGGER.info("Got identity with uid {} and username {}", identity.getUid(), identity.getUsername());
            registryService.getProfileById(identity.getUid());
//            getUserLearningRecords(uid);

        }
    }
    public void getUserLearningRecords(String uid){
        userRecordService.getUserRecord(uid, "");
    }
}
