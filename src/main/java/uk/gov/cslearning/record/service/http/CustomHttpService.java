package uk.gov.cslearning.record.service.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.catalogue.Course;

import java.util.List;
import java.util.Map;

@Service
public class CustomHttpService {
    private final String identitiesMapUrl;
    private final String organisationalUnitRequiredLearningWithinPeriodParamUrl;
    private final String civilServantsCodeParamUrl;
    private HttpService httpService;

    public CustomHttpService(HttpService httpService,
                             @Value("${identity.identitiesMapUrl}") String identitiesMapUrl,
                             @Value("${catalogue.organisationalUnitRequiredLearningWithinPeriodParamUrl}") String organisationalUnitRequiredLearningWithinPeriodParamUrl,
                             @Value("${registry.civilServantsCodeParamUrl}") String civilServantsCodeParamUrl) {
        this.httpService = httpService;
        this.identitiesMapUrl = identitiesMapUrl;
        this.civilServantsCodeParamUrl = civilServantsCodeParamUrl;
        this.organisationalUnitRequiredLearningWithinPeriodParamUrl = organisationalUnitRequiredLearningWithinPeriodParamUrl;
    }

    public Map<String, IdentityDto> getIdentitiesMap() {
        return httpService.getMap(identitiesMapUrl, IdentityDto.class);
    }

    public Map<String, CivilServantDto> getCivilServantMapByOrganisation(String organisationalUnitCode) {
        return httpService.getMap(String.format(civilServantsCodeParamUrl, organisationalUnitCode), CivilServantDto.class);
    }

    public Map<String, List<Course>> getRequiredLearningDueWithinPeriod(long from, long to) {
        return httpService.getMapOfList(String.format(organisationalUnitRequiredLearningWithinPeriodParamUrl, from, to), Course.class);
    }
}
