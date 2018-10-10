package uk.gov.cslearning.record.csrs.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cslearning.record.csrs.domain.CivilServant;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistryServiceTest {
    private URI getCurrentUrl;
    private String findByUidUrlFormat = "%s";

    private OAuth2RestOperations restOperations = mock(OAuth2RestOperations.class);
    private RequestEntityFactory requestEntityFactory = mock(RequestEntityFactory.class);
    private RegistryService registryService;

    @Before
    public void setUp() throws Exception {
        getCurrentUrl = new URI("http://localhost");
        registryService = new RegistryService(restOperations, requestEntityFactory, getCurrentUrl, findByUidUrlFormat);
    }

    @Test
    public void getCurrentReturnsCivilServant() {
        ResponseEntity<CivilServant> responseEntity = mock(ResponseEntity.class);
        CivilServant civilServant = new CivilServant();

        RequestEntity requestEntity = mock(RequestEntity.class);
        when(requestEntityFactory.createGetRequest(getCurrentUrl)).thenReturn(requestEntity);
        when(restOperations.exchange(requestEntity, CivilServant.class)).thenReturn(responseEntity);

        when(responseEntity.getBody()).thenReturn(civilServant);

        assertEquals(Optional.of(civilServant), registryService.getCurrent());
    }

    @Test
    public void getCurrentReturnsEmptyOptionalIfResponseBodyIsNull() {
        ResponseEntity<CivilServant> responseEntity = mock(ResponseEntity.class);

        RequestEntity requestEntity = mock(RequestEntity.class);
        when(requestEntityFactory.createGetRequest(getCurrentUrl)).thenReturn(requestEntity);
        when(restOperations.exchange(requestEntity, CivilServant.class)).thenReturn(responseEntity);

        when(responseEntity.getBody()).thenReturn(null);

        assertEquals(Optional.empty(), registryService.getCurrent());
    }

    @Test
    public void getCivilServantByUidReturnsCivilServant() {
        String uid = "_uid";
        CivilServant civilServant = new CivilServant();

        when(restOperations.getForObject(uid, CivilServant.class)).thenReturn(civilServant);

        assertEquals(Optional.of(civilServant), registryService.getCivilServantByUid(uid));
    }

    @Test
    public void getCivilServantByUidReturnsEmptyOptionalIfExceptionIsThrown() {
        String uid = "_uid";

        HttpClientErrorException exception = mock(HttpClientErrorException.class);

        doThrow(exception).when(restOperations).getForObject(uid, CivilServant.class);

        assertEquals(Optional.empty(), registryService.getCivilServantByUid(uid));
    }

    @Test
    public void getCivilServantByUidReturnsEmptyOptionalIfResponseBodyEmpty() {
        String uid = "_uid";

        when(restOperations.getForObject(uid, CivilServant.class)).thenReturn(null);

        assertEquals(Optional.empty(), registryService.getCivilServantByUid(uid));
    }
}