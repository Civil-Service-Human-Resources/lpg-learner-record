package uk.gov.cslearning.record.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class CSRSStubService {

    public void getCivilServant(String uid, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/csrs/civilServants/resource/" + uid))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getCivilServantsForDepartment(String departmentCode, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/csrs/civilServants/organisation/" + departmentCode))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
