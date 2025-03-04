package uk.gov.cslearning.record.service.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.exception.ClientAuthenticationErrorException;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthService implements IUserAuthService {

    private final SecurityContextService securityContextService;

    public UserAuthService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }


    @Override
    public Jwt getBearerTokenFromUserAuth() {
        Jwt jwt = null;
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = getAuthentication().getPrincipal();
            if (principal instanceof Jwt jwtPrincipal) {
                jwt = jwtPrincipal;
            }
        }
        return jwt;
    }

    @Override
    public String getUsername() {
        String username = "";
        Jwt jwtPrincipal = getBearerTokenFromUserAuth();
        if (jwtPrincipal != null) {
            username = (String) jwtPrincipal.getClaims().get("user_name");
        }
        if (StringUtils.isBlank(username)) {
            throw new ClientAuthenticationErrorException("Learner Id is missing from authentication token");
        }
        return username;
    }

    @Override
    public Boolean userHasRole(String role) {
        return this.getAuthorities().contains(role);
    }

    private Authentication getAuthentication() {
        return securityContextService.getSecurityContext().getAuthentication();
    }

    private List<String> getAuthorities() {
        List<GrantedAuthority> authorities = List.copyOf(this.getAuthentication().getAuthorities());

        if (authorities == null) {
            return new ArrayList<>();
        }
        return authorities.stream().map(GrantedAuthority::getAuthority).toList();
    }
}
