package uk.gov.cslearning.record.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class LearningCatalogueStubService {

    public void getCourse(String courseId, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/courses/" + courseId))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

    public void getRequiredCoursesByDueDaysGroupedByOrg(String dueDaysList, String response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/learning_catalogue/courses"))
                        .withQueryParams(Map.of(
                                "mandatory", equalTo("true"),
                                "days", equalTo(dueDaysList),
                                "size", equalTo("1000000000")))
                        .withHeader("Authorization", equalTo("Bearer token"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
        );
    }

}
