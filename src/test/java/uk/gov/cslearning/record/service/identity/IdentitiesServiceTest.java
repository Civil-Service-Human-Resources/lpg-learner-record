package uk.gov.cslearning.record.service.identity;


import org.junit.jupiter.api.Test;
import uk.gov.cslearning.record.client.identity.IIdentitiesClient;
import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdentitiesServiceTest {

    private final IIdentitiesClient iIdentitiesClient = mock(IIdentitiesClient.class);
    private final IdentitiesService identityService = new IdentitiesService(iIdentitiesClient, 2);

    @Test
    public void shouldFetchIdentitiesInBatches() {
        Identity id1 = new Identity("email1@email.com", "123", List.of());
        Identity id2 = new Identity("email2@email.com", "456", List.of());
        Identity id3 = new Identity("email3@email.com", "789", List.of());
        Identity id4 = new Identity("email4@email.com", "101", List.of());

        Map<String, Identity> batch1 = Map.of("123", id1, "456", id2);
        Map<String, Identity> batch2 = Map.of("789", id3, "101", id4);

        when(iIdentitiesClient.fetchByUids(List.of("123", "456"))).thenReturn(batch1);
        when(iIdentitiesClient.fetchByUids(List.of("789", "101"))).thenReturn(batch2);

        Map<String, Identity> res = identityService.fetchByUids(List.of("123", "456", "789", "101"));
        assertEquals("email1@email.com", res.get("123").getUsername());
        assertEquals("email2@email.com", res.get("456").getUsername());
        assertEquals("email3@email.com", res.get("789").getUsername());
        assertEquals("email4@email.com", res.get("101").getUsername());
    }
}
