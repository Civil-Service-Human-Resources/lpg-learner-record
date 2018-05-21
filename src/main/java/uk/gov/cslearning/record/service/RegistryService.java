package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.service.identity.IdentityService;

@Service
public class RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRecordService.class);

    @Autowired
    private IdentityService identityService;

    @Autowired
    public RegistryService() {
    }

    public void getProfileById(String id){
        Object[] response = identityService.httpGet("http://localhost:9002/civilServants/" + id);
        for (Object o: response){
            System.out.println(o);
            System.out.println("");
        }
    }
}
