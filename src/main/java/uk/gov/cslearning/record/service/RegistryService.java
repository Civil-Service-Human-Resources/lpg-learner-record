package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRecordService.class);

    private RestTemplate restTemplate;

    private String findByUidUrlFormat;

    @Autowired
    public RegistryService(RestTemplate restTemplate, @Value("${registry.findByUidUrlFormat}") String findByUidUrlFormat) {
        this.restTemplate = restTemplate;
        this.findByUidUrlFormat = findByUidUrlFormat;
    }

    public CivilServant getCivilServantByUid(String uid) {
        LOGGER.debug("Getting profile details for civil servant with UID {}", uid);

        Map response = restTemplate.getForObject(String.format(findByUidUrlFormat, uid), Map.class);

        CivilServant civilServant = new CivilServant();
        civilServant.setAreaOfWork(getProperty(response, "profession.name"));
        civilServant.setDepartmentCode(getProperty(response, "organisation.department.code"));
        civilServant.setGradeCode(getProperty(response, "grade.code"));

        return civilServant;
    }

    private String getProperty(Map data, String path) {
        String[] keys = path.split("\\.");
        Map current = data;
        for (int i = 0; i < keys.length; i++) {
            if (current == null) {
                break;
            }
            String key = keys[i];
            if (!current.containsKey(key)) {
                break;
            }
            if (i == keys.length - 1) {
                return (String) current.get(key);
            }
            current = (Map) current.get(key);
        }
        return null;
    }
}
