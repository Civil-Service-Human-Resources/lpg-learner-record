package uk.gov.cslearning.record.csrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import edu.emory.mathcs.backport.java.util.Collections;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);
    private final RequestEntityFactory requestEntityFactory;
    private OAuth2RestOperations restOperations;
    private String findByUidUrlFormat;
    private String getResourceByUidUrl;
    private String getResourceByOrgCodeUrl;
    private URI getCurrentUrl;

    @Autowired
    public RegistryService(OAuth2RestOperations restOperations,
                           RequestEntityFactory requestEntityFactory,
                           @Value("${registry.getCurrentUrl}") URI getCurrentUrl,
                           @Value("${registry.getResourceByUidUrl}") String getResourceByUidUrl,
                           @Value("${registry.findByUidUrlFormat}") String findByUidUrlFormat,
                           @Value("${registry.getResourceByOrgCodeUrl}") String getResourceByOrgCodeUrl) {
        this.restOperations = restOperations;
        this.requestEntityFactory = requestEntityFactory;
        this.findByUidUrlFormat = findByUidUrlFormat;
        this.getCurrentUrl = getCurrentUrl;
        this.getResourceByUidUrl = getResourceByUidUrl;
        this.getResourceByOrgCodeUrl = getResourceByOrgCodeUrl;
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<CivilServant> getCurrent() {
        LOGGER.debug("Getting profile details for authenticated user");
        RequestEntity requestEntity = requestEntityFactory.createGetRequest(getCurrentUrl);

        ResponseEntity<CivilServant> response = restOperations.exchange(requestEntity, CivilServant.class);

        return Optional.ofNullable(response.getBody());
    }

    public Optional<CivilServant> getCivilServantByUid(String uid) {
        LOGGER.info("Getting profile details for civil servant with UID {}", uid);
        LOGGER.debug("URL {}", String.format(findByUidUrlFormat, uid));

        try {
            CivilServant civilServant = restOperations.getForObject(String.format(findByUidUrlFormat, uid), CivilServant.class);
            return Optional.ofNullable(civilServant);
        } catch (HttpClientErrorException e) {
            LOGGER.info(String.format("Cannot find profile details for civil servant with UID %s", uid), e);
        }

        return Optional.empty();
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

    public List<CivilServant> getCivilServantsByOrgCode(String code) {
        LOGGER.debug("Getting profile details for civil servants with organisation code {}", code);
        LOGGER.debug("URL {}", String.format(getResourceByOrgCodeUrl, code));
        RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(getResourceByOrgCodeUrl, code));

        try {
            return restOperations.exchange(requestEntity, new ParameterizedTypeReference<List<CivilServant>>(){}).getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.debug(String.format("Cannot find profile details for civil servant with organisation code %s", code), e);
        }
        return Collections.emptyList();
    }
}
