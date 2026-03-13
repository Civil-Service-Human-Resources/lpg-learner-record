package uk.gov.cslearning.record.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IdentitiesClient implements IIdentitiesClient {
    private final IHttpClient httpClient;
    private final String uidMapUrl;

    public IdentitiesClient(
            @Qualifier("identitiesHttpClient") IHttpClient httpClient,
            @Value("${identity.UidMapUrl}") String uidMapUrl
    ) {
        this.httpClient = httpClient;
        this.uidMapUrl = uidMapUrl;
    }

    @Override
    public Map<String, Identity> fetchByUids(List<String> uids) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(uidMapUrl).queryParam("uids", String.join(",", uids));
        RequestEntity<Void> request = RequestEntity.get(uri.build().toUriString()).build();
        return httpClient.executeMapRequest(request, new ParameterizedTypeReference<>() {
        });
    }

}
