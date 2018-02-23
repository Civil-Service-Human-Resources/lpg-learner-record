package uk.gov.cslearning.record.service.xapi;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Service
public class XApiService implements Serializable {

    private static final DateTimeFormatter COMPLETION_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE_TIME)
            .appendLiteral('Z')
            .toFormatter();

    private static final Logger LOGGER = LoggerFactory.getLogger(XApiService.class);

    private String authorisation;

    private String baseUrl;

    @Autowired
    public XApiService(@Value("${xapi.authorisation}") String authorisation,
                       @Value("${xapi.url}") String baseUrl) {
        checkArgument(authorisation != null);
        checkArgument(baseUrl != null);
        this.authorisation = authorisation;
        this.baseUrl = baseUrl;
    }

    public Collection<Statement> getStatements(String userId, String activityId) {
        LOGGER.debug("Getting XApi statements for user {} and activity {}", userId, activityId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Experience-API-Version", "1.0.3");
        headers.add("Authorization", "Basic " + authorisation);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Gson gson = new Gson();
        Agent agent = new Agent(userId);

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("agent", gson.toJson(agent));

        String url = baseUrl + "/statements?agent={agent}";

        if (activityId != null) {
            urlVariables.put("activity", activityId);
            url += "&activity={activity}";
        }

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class, urlVariables);

        List<Statement> statements = new ArrayList<>();

        if (response.getStatusCode() != HttpStatus.OK) {
            return statements;
        }

        Map data = gson.fromJson(response.getBody(), Map.class);

        Collection<Map> rawStatements = (Collection) data.get("statements");

        for (Map rawStatement : rawStatements) {
            Map object = (Map) rawStatement.get("object");
            if (object == null || !"Activity".equals(object.get("objectType"))) {
                continue;
            }
            Map rawVerb = (Map) rawStatement.get("verb");
            if (rawVerb == null) {
                continue;
            }
            Map result = (Map) rawStatement.get("result");
            String timestamp = (String) rawStatement.get("timestamp");
            String objectId = (String) object.get("id");
            String verbId = (String) rawVerb.get("id");
            String score = null;
            if (result != null) {
                score = (String) result.get("score");
            }
            Verb verb = Verb.fromUri(verbId);
            if (verb == null) {
                LOGGER.debug("Unrecognised verb {}, ignoring statement.", verbId);
            } else {
                statements.add(new Statement(objectId, verb, score,
                        LocalDateTime.parse(timestamp, COMPLETION_DATE_FORMATTER)));
            }
        }

        return statements;
    }
}
