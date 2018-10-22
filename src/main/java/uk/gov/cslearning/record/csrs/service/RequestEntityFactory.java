package uk.gov.cslearning.record.csrs.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class RequestEntityFactory {
    public RequestEntity createGetRequest(URI uri) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + details.getTokenValue());
        return new RequestEntity(headers, HttpMethod.GET, uri);
    }

    public RequestEntity createGetRequest(String uri) {
        try {
            return createGetRequest(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RequestEntityException(e);
        }
    }
}
