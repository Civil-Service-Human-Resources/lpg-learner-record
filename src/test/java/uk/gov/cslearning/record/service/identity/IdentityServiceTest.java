package uk.gov.cslearning.record.service.identity;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.oauth2.client.OAuth2RestOperations;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceTest {

    private static final String LIST_ALL_URL = "http://localhost/identities";

    private IdentityService identityService;

    @Mock
    private OAuth2RestOperations restOperations;

    @Before
    public void setup() {
        initMocks(this);
        identityService = new IdentityService(restOperations, LIST_ALL_URL);
    }

    @Test
    public void shouldReturnEmptySetForNoIdentities() {
        when(restOperations.getForObject(any(), any())).thenReturn(new Identity[]{});
        assertThat(identityService.listAll(), hasSize(0));
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