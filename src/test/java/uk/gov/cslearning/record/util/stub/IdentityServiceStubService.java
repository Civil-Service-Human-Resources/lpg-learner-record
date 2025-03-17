package uk.gov.cslearning.record.util.stub;


import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class IdentityServiceStubService {

    public void getClientToken() {
        stubFor(
                WireMock.post(urlPathEqualTo("/identity/oauth/token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "access_token": "token",
                                            "token_type": "Bearer",
                                            "expires_in": 43199
                                        }"""))
        );
    }

    public void getIdentitiesMap(List<String> uids, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/identity/api/identities/map-for-uids"))
                        .withQueryParams(Map.of("uids", equalTo(String.join(",", uids))))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response)
                        ));
    }

    public void getIdentityWithEmail(String emailAddress, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/identity/api/identities"))
                        .withQueryParams(Map.of("emailAddress", equalTo(emailAddress)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response)
                        ));
    }

}
