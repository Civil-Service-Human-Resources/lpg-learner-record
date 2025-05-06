package uk.gov.cslearning.record.util.stub;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class NotificationServiceStubService {

    public void validateSentEmails(String emailName, int expectedSentCount) {
        int sentCount = getSentEmails(emailName).size();
        assertEquals(expectedSentCount, sentCount, String.format("Expected %s '%s' emails to be sent but %s were sent",
                expectedSentCount, emailName, sentCount));
    }

    public List<LoggedRequest> getSentEmails(String emailName) {
        return WireMock.findAll(postRequestedFor(urlPathEqualTo("/notification_service/notifications/emails/" + emailName + "/send")));
    }

    public void sendEmail(String emailName, String expBody) {
        WireMock.
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
