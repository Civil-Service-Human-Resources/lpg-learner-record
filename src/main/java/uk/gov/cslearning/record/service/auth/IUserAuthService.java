package uk.gov.cslearning.record.service.auth;

import org.springframework.security.oauth2.jwt.Jwt;

public interface IUserAuthService {

    Jwt getBearerTokenFromUserAuth();

    String getUsername();

    Boolean userHasRole(String role);
}
