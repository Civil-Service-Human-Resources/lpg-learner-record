package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
@PowerMockIgnore("javax.security.auth.*")
public class RequestEntityFactoryHttpTest {
    private RequestEntityFactory requestEntityFactory = new RequestEntityFactory();

    @Test
    public void createGetRequestSetsAuthenticationHeaders() throws URISyntaxException {
        URI uri = new URI("http://localhost");

        mockStatic(SecurityContextHolder.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = mock(OAuth2AuthenticationDetails.class);
        String tokenValue = "token-value";

        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getDetails()).thenReturn(oAuth2AuthenticationDetails);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn(tokenValue);


        RequestEntity requestEntity = requestEntityFactory.createGetRequest(uri);

        assertEquals(HttpMethod.GET, requestEntity.getMethod());
        assertEquals("Bearer token-value", requestEntity.getHeaders().get("Authorization").get(0));
        assertEquals(uri, requestEntity.getUrl());
    }


    @Test
    public void createGetRequestSetsAuthenticationHeadersWithUriString() throws URISyntaxException {
        String uri = "http://localhost";

        mockStatic(SecurityContextHolder.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = mock(OAuth2AuthenticationDetails.class);
        String tokenValue = "token-value";

        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getDetails()).thenReturn(oAuth2AuthenticationDetails);
        when(oAuth2AuthenticationDetails.getTokenValue()).thenReturn(tokenValue);


        RequestEntity requestEntity = requestEntityFactory.createGetRequest(uri);

        assertEquals(HttpMethod.GET, requestEntity.getMethod());
        assertEquals("Bearer token-value", requestEntity.getHeaders().get("Authorization").get(0));
        assertEquals(new URI(uri), requestEntity.getUrl());
    }

    @Test
    public void createGetRequestCatchesUriSyntaxException() throws URISyntaxException {
        String uri = "httpåå://localhost";

        try {
            requestEntityFactory.createGetRequest(uri);
            fail("Expected RequestEntityException");
        } catch (RequestEntityException e) {
            assertTrue(e.getCause() instanceof URISyntaxException);
        }
    }
}