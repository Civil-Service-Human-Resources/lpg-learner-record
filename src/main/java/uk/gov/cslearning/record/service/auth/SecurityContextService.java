package uk.gov.cslearning.record.service.auth;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextService {

    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }
}
