package uk.gov.cslearning.record.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.identity.oauth.OAuthClient;
import uk.gov.cslearning.record.domain.identity.OAuthToken;

import java.time.LocalDateTime;

@Slf4j
@Service
public class IdentityService {

    private final OAuthClient oAuthClient;

    public IdentityService(OAuthClient oAuthClient) {
        this.oAuthClient = oAuthClient;
    }

    @Cacheable("service-token")
    public OAuthToken getCachedOAuthServiceToken() {
        OAuthToken oAuthToken = new OAuthToken();
        try {
            log.info("Fetching OAuth client token from identity service");
            oAuthToken = oAuthClient.getAccessToken();
            oAuthToken.setExpiryDateTime(LocalDateTime.now().plusSeconds(oAuthToken.getExpiresIn()));
        } catch (Exception e) {
            log.error("Unable to retrieve service token from identity-service. " +
                    "Following response is received from identity-service: {}", e.getMessage());
        }
        return oAuthToken;
    }

    @CacheEvict(value = "service-token", allEntries = true)
    public void removeServiceTokenFromCache() {
        log.info("IdentityService.removeServiceTokenFromCache: service token is removed from the cache.");
    }

}
