package uk.gov.cslearning.record.csrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);
    private final RequestEntityFactory requestEntityFactory;
    private OAuth2RestOperations restOperations;
    private String findByUidUrlFormat;
    private String getResourceByUidUrl;
    private String getAllCivilServants;
    private URI getCurrentUrl;

    @Autowired
    public RegistryService(OAuth2RestOperations restOperations,
                           RequestEntityFactory requestEntityFactory,
                           @Value("${registry.getCurrentUrl}") URI getCurrentUrl,
                           @Value("${registry.getResourceByUidUrl}") String getResourceByUidUrl,
                           @Value("${registry.findByUidUrlFormat}") String findByUidUrlFormat,
                           @Value("${registry.getAllCivilServants}") String getAllCivilServants) {
        this.restOperations = restOperations;
        this.requestEntityFactory = requestEntityFactory;
        this.findByUidUrlFormat = findByUidUrlFormat;
        this.getCurrentUrl = getCurrentUrl;
        this.getResourceByUidUrl = getResourceByUidUrl;
        this.getAllCivilServants = getAllCivilServants;
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<CivilServant> getCurrent() {
        LOGGER.debug("Getting profile details for authenticated user");
        RequestEntity requestEntity = requestEntityFactory.createGetRequest(getCurrentUrl);

        ResponseEntity<CivilServant> response = restOperations.exchange(requestEntity, CivilServant.class);

        return Optional.ofNullable(response.getBody());
    }

    public Optional<CivilServant> getCivilServantByUid(String uid) {
        LOGGER.debug("Getting profile details for civil servant with UID {}", uid);
        LOGGER.debug("URL {}", String.format(findByUidUrlFormat, uid));

        try {
            CivilServant civilServant = restOperations.getForObject(String.format(findByUidUrlFormat, uid), CivilServant.class);
            return Optional.ofNullable(civilServant);
        } catch (HttpClientErrorException e) {
            LOGGER.debug(String.format("Cannot find profile details for civil servant with UID %s", uid), e);
        }

        return Optional.empty();
    }

    public List<CivilServant> getAllCivilServants() {
        LOGGER.debug("Getting all civil servants");

        try {
            CivilServant[] civilServants = restOperations.getForObject(getAllCivilServants, CivilServant[].class);
            if (civilServants != null) {
                return Arrays.asList(civilServants);
            } else {
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.debug(String.format("Error when fetching civil servants: %s", e));
        }

        return Collections.emptyList();
    }

    public Optional<CivilServant> getCivilServantResourceByUid(String uid) {
        LOGGER.debug("Getting profile details for civil servant with UID {}", uid);
        LOGGER.debug("URL {}", String.format(getResourceByUidUrl, uid));

        try {
            CivilServant civilServant = restOperations.getForObject(String.format(getResourceByUidUrl, uid), CivilServant.class);
            return Optional.ofNullable(civilServant);
        } catch (HttpClientErrorException e) {
            LOGGER.debug(String.format("Cannot find profile details for civil servant with UID %s", uid), e);
        }
        return Optional.empty();
    }
}
