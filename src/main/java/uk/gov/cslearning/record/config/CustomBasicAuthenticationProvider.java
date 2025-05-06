package uk.gov.cslearning.record.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class CustomBasicAuthenticationProvider implements AuthenticationProvider {

    @Value("${spring.security.user.name}")
    private String validUsername;

    @Value("${spring.security.user.password}")
    private String validPassword;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (validUsername.equalsIgnoreCase(username) && validPassword.equals(password)) {
            log.debug("Basic authenticate successful for user: {}", username);
            return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        } else {
            log.debug("Basic authenticate failed for user: {}. Invalid username and/or password.", username);
            throw new BadCredentialsException("Basic authenticate failed for user: " + username
                    + ". Invalid username and/or password.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
