package uk.gov.cslearning.record.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.service.UserRecordService;
import uk.gov.cslearning.record.service.HttpService;

import java.util.LinkedHashMap;

public class LearningJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);
    public static final String IDENTITY_URL = "http://identity.local.cshr.digital:8081/api/identities";

    private HttpService httpService;

    @Autowired
    private UserRecordService userRecordService;

    public LearningJob(HttpService httpService) {
        this.httpService = httpService;
    }

    public void getIdentityListFromResponse() {
        Object [] response = httpService.httpGet(IDENTITY_URL);
        for (Object o: response){
            LinkedHashMap<String, String> identity = (LinkedHashMap<String, String>) o;
            String uid = identity.get("uid");
            String username = identity.get("username");
            LOGGER.info("Got identity with uid {} and username {}", uid, username);


//            getUserLearningRecords(uid);

        }
    }

    public void getUserLearningRecords(String uid){

        userRecordService.getUserRecord(uid, "");

    }
}
