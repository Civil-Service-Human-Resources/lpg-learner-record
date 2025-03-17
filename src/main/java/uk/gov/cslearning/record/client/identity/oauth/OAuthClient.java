package uk.gov.cslearning.record.client.identity.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.domain.identity.OAuthToken;

@Slf4j
@Component
public class OAuthClient implements IOAuthClient {
    private final IHttpClient client;
    @Value("${oauth.tokenUrl}")
    private String oauthTokenUrl;

    public OAuthClient(@Qualifier("oAuthClient") IHttpClient client) {
        this.client = client;
    }

    @Override
    public OAuthToken getAccessToken() {

        RequestEntity<Void> request = RequestEntity
                .post(oauthTokenUrl + "?grant_type=client_credentials").build();
        return client.executeRequest(request, OAuthToken.class);
    }
}
