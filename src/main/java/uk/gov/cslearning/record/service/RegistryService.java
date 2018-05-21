package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRecordService.class);

    private OAuth2RestOperations restOperations;

    @Autowired
    public RegistryService() {
    }

    public CivilServant getCivilServantByUid(String uid){
        Map<String, Object> response = restOperations.getForObject("http://localhost:9002/civilServants/" + uid, Map.class);
        CivilServant civilServant = new CivilServant();
        if (response.containsKey("organisation")) {
            Map<String, Object> organisation = (Map<String, Object>) response.get("organisation");
            if (organisation.containsKey("department")){
                Map<String, Object> department = (Map<String, Object>) organisation.get("department");
                civilServant.setDepartmentCode((String) department.get("code"));
            }
        }
        return civilServant;
    }
}
