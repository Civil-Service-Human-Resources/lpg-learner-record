package uk.gov.cslearning.record.client.identity.oauth;

import uk.gov.cslearning.record.domain.identity.OAuthToken;

public interface IOAuthClient {
    OAuthToken getAccessToken();
}
