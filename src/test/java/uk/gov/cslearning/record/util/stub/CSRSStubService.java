package uk.gov.cslearning.record.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public void getCivilServantsForDepartment(String departmentCode, Integer page, Integer size, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/csrs/civilServants/organisation/" + departmentCode))
                        .withQueryParams(Map.of("page", equalTo(page.toString()), "size", equalTo(size.toString())))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getDepartments(Integer page, Integer size, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/csrs/v2/organisationalUnits"))
                        .withQueryParams(Map.of("page", equalTo(page.toString()), "size", equalTo(size.toString())))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }
}
