package uk.gov.cslearning.record.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.identity.OAuthToken;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.micrometer.common.util.StringUtils.isBlank;

@Service
@Slf4j
public class BearerTokenService implements IBearerTokenService {
    private final Clock clock;
    private final IdentityService identityService;
    private final IUserAuthService userAuthService;
    @Value("${oauth.refresh.serviceTokenCache.beforeSecondsToExpire}")
    private long refreshServiceTokenCacheBeforeSecondsToExpire;

    public BearerTokenService(Clock clock, IdentityService identityService, IUserAuthService userAuthService) {
        this.clock = clock;
        this.identityService = identityService;
        this.userAuthService = userAuthService;
    }

    public String getBearerToken() {
        String bearerToken = "";
        Jwt jwtPrincipal = userAuthService.getBearerTokenFromUserAuth();
        if (jwtPrincipal != null) {
            bearerToken = jwtPrincipal.getTokenValue();
        }
        if (isBlank(bearerToken)) {
            OAuthToken serviceToken = identityService.getCachedOAuthServiceToken();
            LocalDateTime tokenExpiry = serviceToken.getExpiryDateTime();
            log.debug("serviceToken: expiryDateTime: {}", tokenExpiry);
            long secondsRemainingToExpire = tokenExpiry != null ?
                    ChronoUnit.SECONDS.between(LocalDateTime.now(clock), tokenExpiry) : 0;
            log.debug("serviceToken: seconds remaining to service token expiry: {}", secondsRemainingToExpire);
            log.debug("serviceToken: seconds remaining to refresh the service token cache: {}",
                    (secondsRemainingToExpire - refreshServiceTokenCacheBeforeSecondsToExpire));
            if (secondsRemainingToExpire <= refreshServiceTokenCacheBeforeSecondsToExpire) {
                identityService.removeServiceTokenFromCache();
                serviceToken = identityService.getCachedOAuthServiceToken();
            }
            bearerToken = serviceToken.getAccessToken();
        }
        return bearerToken;
    }
}
