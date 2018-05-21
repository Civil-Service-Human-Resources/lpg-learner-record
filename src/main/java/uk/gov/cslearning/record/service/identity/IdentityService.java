package uk.gov.cslearning.record.service.identity;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.emptySet;

@Service
public class IdentityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityService.class);

    private OAuth2RestOperations restOperations;

    private String listAllIdentitiesUrl;

    @Autowired
    public IdentityService(OAuth2RestOperations restOperations, @Value("${identity.listAllUrl}") String listAllIdentitiesUrl) {
        this.restOperations = restOperations;
        this.listAllIdentitiesUrl = listAllIdentitiesUrl;
    }

    public OAuth2AccessToken getAccessToken() {
        return restOperations.getAccessToken();
    }

    public Collection<Identity> listAll() {
        LOGGER.debug("Retrieving all identities");
        Identity[] identities = restOperations.getForObject(listAllIdentitiesUrl, Identity[].class);
        if (identities != null) {
            return Sets.newHashSet(identities);
        }
        return emptySet();
    }
}
