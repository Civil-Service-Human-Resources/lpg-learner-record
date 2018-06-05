package uk.gov.cslearning.record.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
public class RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);

    private OAuth2RestOperations restOperations;

    private String findByUidUrlFormat;

    private URI getCurrentUrl;

    @Autowired
    public RegistryService(OAuth2RestOperations restOperations,
                           @Value("${registry.getCurrentUrl}") URI getCurrentUrl,
                           @Value("${registry.findByUidUrlFormat}") String findByUidUrlFormat) {
        this.restOperations = restOperations;
        this.findByUidUrlFormat = findByUidUrlFormat;
        this.getCurrentUrl = getCurrentUrl;
    }

    @PreAuthorize("isAuthenticated()")
    public CivilServant getCurrent() {
        LOGGER.debug("Getting profile details for authenticated user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + details.getTokenValue());

        RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, getCurrentUrl);

        ResponseEntity<Map> response = restOperations.exchange(requestEntity, Map.class);

        if (response.hasBody()) {
            Map data = response.getBody();
            CivilServant civilServant = new CivilServant();
            civilServant.setProfession(getProperty(data, "profession.name"));
            civilServant.setDepartmentCode(getProperty(data, "organisation.department.code"));
            civilServant.setGradeCode(getProperty(data, "grade.code"));
            return civilServant;
        }
        return null;
    }

    public Optional<CivilServant> getCivilServantByUid(String uid) {
        LOGGER.debug("Getting profile details for civil servant with UID {}", uid);
        LOGGER.debug("URL {}", String.format(findByUidUrlFormat, uid));
        CivilServant civilServant = new CivilServant();
        try {
            Map response = restOperations.getForObject(String.format(findByUidUrlFormat, uid), Map.class);
            civilServant.setFullName(getProperty(response, "fullName"));
            civilServant.setProfession(getProperty(response, "profession.name"));
            civilServant.setDepartmentCode(getProperty(response, "organisation.department.code"));
            civilServant.setGradeCode(getProperty(response, "grade.code"));

        } catch (HttpClientErrorException ex){
            LOGGER.error(ex.getMessage());
        }
        return Optional.of(civilServant);
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
