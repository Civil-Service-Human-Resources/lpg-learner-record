package uk.gov.cslearning.record.service;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Component
public class RequestEntityFactory {
    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Value("${oauth.tokenUrl}")
    private String clientUrl;


    public RequestEntity createGetRequest(URI uri) {
        HttpHeaders headers = createHttpHeaders();
        return new RequestEntity(headers, HttpMethod.GET, uri);
    }

    public RequestEntity createGetRequest(String uri) {
        try {
            return createGetRequest(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RequestEntityException(e);
        }
    }

    public RequestEntity createPostRequest(URI uri, Object body) {
        HttpHeaders headers = createHttpHeaders();
        return new RequestEntity(body, headers, HttpMethod.POST, uri);
    }

    public RequestEntity createPostRequest(String uri, Object body) {
        try {
            return createPostRequest(new URI(uri), body);
        } catch (URISyntaxException e) {
            throw new RequestEntityException(e);
        }
    }

    private HttpHeaders createHttpHeaders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String token;
        if (authentication != null) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            token = details.getTokenValue();
        } else {
            token = getNewAccessToken();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private String getNewAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        String access_token_url = clientUrl;
        access_token_url += "?grant_type=client_credentials";

        ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());

        return jsonObject.getString("access_token");
    }
}
