package uk.gov.cslearning.record.service.identity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDTO;
import uk.gov.cslearning.record.service.catalogue.Course;
import uk.gov.cslearning.record.service.http.HttpService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class CustomHttpService {

    private final URI identitiesListUrl;
    private final URI civilServantUri;
    private final URI organisationalUnitRequiredLearningUrl;
    private HttpService httpService;

    public CustomHttpService(HttpService httpService,
                             @Value("${identity.identitiesMapUrl}") URI identitiesListUrl,
                             @Value("${catalogue.organisationalUnitRequiredLearningUrl}") URI organisationalUnitRequiredLearningUrl,
                             @Value("${registry.civilServantsUrl}") URI civilServantUri) {
        this.httpService = httpService;
        this.identitiesListUrl = identitiesListUrl;
        this.civilServantUri = civilServantUri;
        this.organisationalUnitRequiredLearningUrl = organisationalUnitRequiredLearningUrl;
    }

    public Map<String, IdentityDTO> getIdentitiesMap() {
        return httpService.getMap(identitiesListUrl, IdentityDTO.class);
    }

    public Map<String, CivilServantDto> getCivilServantMap() {
        return httpService.getMap(civilServantUri, CivilServantDto.class);
    }

    public Map<String, List<Course>> getOrganisationalUnitRequiredLearning() {
        return httpService.getMapOfList(organisationalUnitRequiredLearningUrl, Course.class);
    }
}
