package uk.gov.cslearning.record.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class IdentitiesClient implements IIdentitiesClient {
    private final IHttpClient httpClient;
    private final String identitiesUrl;
    private final String uidMapUrl;

    public IdentitiesClient(
            @Qualifier("identitiesHttpClient") IHttpClient httpClient,
            @Value("${identity.identitiesUrl}") String identitiesUrl,
            @Value("${identity.UidMapUrl}") String uidMapUrl
    ) {
        this.httpClient = httpClient;
        this.identitiesUrl = identitiesUrl;
        this.uidMapUrl = uidMapUrl;
    }

    @Override
    public Map<String, Identity> fetchByUids(List<String> uids) {
        String url = String.format("%s?uids=%s", uidMapUrl, String.join(",", uids));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeMapRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public Optional<Identity> getIdentityWithEmail(String email) {
        String url = String.format("%s?emailAddress=%s", identitiesUrl, email);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return Optional.ofNullable(httpClient.executeRequest(request, Identity.class));
    }

}
