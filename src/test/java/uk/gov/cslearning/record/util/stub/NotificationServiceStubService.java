package uk.gov.cslearning.record.util.stub;


import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class NotificationServiceStubService {

    public void sendEmail(String emailName, String expBody) {
        stubFor(
                WireMock.post(urlPathEqualTo("/notification_service/notifications/emails/" + emailName + "/send"))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .withRequestBody(equalToJson(expBody, true, true))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(expBody))
        );
    }

}
