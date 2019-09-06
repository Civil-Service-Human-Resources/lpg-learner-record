package uk.gov.cslearning.record.service.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.catalogue.Course;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomHttpServiceTest {

    private String identitiesMapUrl = "identityUrl";
    private String organisationalUnitRequiredLearningUrl = "orgUnitUrl";
    private String organisationalUnitRequiredLearningWithinPeriodParamUrl = "orgUnitReqUrl?days=%s";
    private String civilServantCodeUri = "civilServantCodeUrl";
    private String civilServantsMapUrl = "civilServantsMapUrl";
    private String civilServantsCodeParamUrl = "civilServantCodeParamUrl?code=%s";

    @Mock
    private HttpService httpService;

    private CustomHttpService customHttpService;

    @Before
    public void setUp() throws Exception {
        customHttpService = new CustomHttpService(httpService,
                identitiesMapUrl,
                organisationalUnitRequiredLearningUrl,
                organisationalUnitRequiredLearningWithinPeriodParamUrl,
                civilServantCodeUri,
                civilServantsMapUrl,
                civilServantsCodeParamUrl);
    }

    @Test
    public void shouldGetIdentitiesMap() {
        customHttpService.getIdentitiesMap();

        verify(httpService).getMap(identitiesMapUrl, IdentityDto.class);
    }

    @Test
    public void shouldGetCivilServantsByOrganisationalUnitCodeMap() {
        customHttpService.getCivilServantsByOrganisationalUnitCodeMap();

        verify(httpService).getMap(civilServantCodeUri, CivilServantDto.class);
    }

    @Test
    public void getCivilServantMapByOrganisation() {
        String code = "code";
        customHttpService.getCivilServantMapByOrganisation(code);

        verify(httpService).getMap(String.format(civilServantsCodeParamUrl, code), CivilServantDto.class);
    }

    @Test
    public void getOrganisationalUnitRequiredLearning() {
        customHttpService.getOrganisationalUnitRequiredLearning();

        verify(httpService).getMapOfList(organisationalUnitRequiredLearningUrl, Course.class);
    }

    @Test
    public void getRequiredLearningDueWithinPeriod() {
        long from = 1L;
        long to = 7L;

        customHttpService.getRequiredLearningDueWithinPeriod(from, to);

        verify(httpService).getMapOfList(String.format(organisationalUnitRequiredLearningWithinPeriodParamUrl, from, to), Course.class);
    }
}