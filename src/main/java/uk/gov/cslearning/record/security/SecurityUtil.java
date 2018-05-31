package uk.gov.cslearning.record.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public final class SecurityUtil {

    public static boolean hasAuthority(String authorityName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorites = authentication.getAuthorities();

        for (GrantedAuthority authority : authorites) {
            if (authority.getAuthority().equals(authorityName)) {
                return true;
            }
        }

        return false;
    }
}
