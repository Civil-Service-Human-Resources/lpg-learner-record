package uk.gov.cslearning.record.service.identity;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.ws.http.HTTPException;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceTest {

    private static final String LIST_ALL_URL = "http://localhost/identities";

    private static final String API_URL = "http://localhost/api/identities";

    private IdentityService identityService;

    @Mock
    private OAuth2RestOperations restOperations;

    @Before
    public void setup() {
        initMocks(this);
        identityService = new IdentityService(restOperations, LIST_ALL_URL, API_URL);
    }

    @Test
    public void shouldReturnEmptySetForNoIdentities() {
        when(restOperations.getForObject(any(), any())).thenReturn(new Identity[]{});
        assertThat(identityService.listAll(), hasSize(0));
    }

    @Test
    public void shouldReturnIdentityWithEmailAddress() {
        Identity identity = new Identity();
        identity.setUid("uid");
        identity.setUsername("username");

        when(restOperations.getForObject(API_URL + "?emailAddress=test@domain.com", Identity.class)).thenReturn(identity);

        Optional<Identity> returnedIdentity = identityService.getIdentityByEmailAddress("test@domain.com");

        assertEquals(returnedIdentity.get().getUid(), "uid");
        assertEquals(returnedIdentity.get().getUsername(), "username");
    }

    @Test
    public void shouldReturnNullIfIdentityDoesNotExist() {
        when(restOperations.getForObject(API_URL + "?emailAddress=test@domain.com", Identity.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<Identity> returnedIdentity =  identityService.getIdentityByEmailAddress("test@domain.com");

        assertTrue(returnedIdentity.equals(Optional.empty()));
    }

    @Test
    public void shouldReturnIdentities() {

        Identity identity = new Identity();
        identity.setUid("uid");
        identity.setUsername("username");

        when(restOperations.getForObject(LIST_ALL_URL, Identity[].class)).thenReturn(new Identity[]{identity});

        Collection<Identity> identities = identityService.listAll();

        assertThat(identities, hasSize(1));
        assertThat(Iterables.get(identities, 0).getUid(), equalTo("uid"));
        assertThat(Iterables.get(identities, 0).getUsername(), equalTo("username"));
    }
}