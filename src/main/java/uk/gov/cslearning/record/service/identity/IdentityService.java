package uk.gov.cslearning.record.service.identity;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.service.CivilServant;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.emptySet;

@Service
public class IdentityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityService.class);

    private OAuth2RestOperations restOperations;

    private String listAllIdentitiesUrl;
    private String identityAPIUrl;

    @Autowired
    public IdentityService(OAuth2RestOperations restOperations,
                           @Value("${identity.listAllUrl}") String listAllIdentitiesUrl,
                           @Value("${identity.identityAPIUrl}") String identityAPIUrl) {
        this.restOperations = restOperations;
        this.listAllIdentitiesUrl = listAllIdentitiesUrl;
        this.identityAPIUrl = identityAPIUrl;
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

    public String getEmailAddress(String uid) {

        LOGGER.debug("Getting email address for civil servant {}", uid);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl)
                .queryParam("uid", uid);

        Optional<Identity> identity = getIdentity(builder.toUriString());

        if (identity.isPresent()) {
            return identity.get().getUsername();
        }
        return null;
    }

    public Optional<Identity> getIdentityByEmailAddress(String emailAddress){
        LOGGER.debug("Getting identity with email address {}", emailAddress);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(identityAPIUrl).queryParam("emailAddress", emailAddress);

        return getIdentity(builder.toUriString());
    }

    private Optional<Identity> getIdentity(String path){
        Identity identity;
        try{
            identity = restOperations.getForObject(path, Identity.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }
        return Optional.of(identity);
    }
}
