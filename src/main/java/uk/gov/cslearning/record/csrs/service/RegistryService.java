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
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.service.RequestEntityFactory;

import java.net.URI;
import java.util.Optional;

@Service
public class RegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryService.class);

    private OAuth2RestOperations restOperations;

    private String findByUidUrlFormat;

    private URI getCurrentUrl;

    private final RequestEntityFactory requestEntityFactory;

    @Autowired
    public RegistryService(OAuth2RestOperations restOperations,
                           RequestEntityFactory requestEntityFactory,
                           @Value("${registry.getCurrentUrl}") URI getCurrentUrl,
                           @Value("${registry.findByUidUrlFormat}") String findByUidUrlFormat) {
        this.restOperations = restOperations;
        this.requestEntityFactory = requestEntityFactory;
        this.findByUidUrlFormat = findByUidUrlFormat;
        this.getCurrentUrl = getCurrentUrl;
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
        } catch (HttpClientErrorException e){
            LOGGER.error(String.format("Cannot find profile details for civil servant with UID %s", uid), e);
        }

        return Optional.empty();
    }
}
