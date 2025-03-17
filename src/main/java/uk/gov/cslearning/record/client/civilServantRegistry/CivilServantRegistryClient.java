package uk.gov.cslearning.record.client.civilServantRegistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.csrs.domain.CivilServant;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CivilServantRegistryClient implements ICivilServantRegistryClient {

    private final IHttpClient httpClient;
    private final String getResourceByUidUrl;
    private final String getResourceByOrgCodeUrl;

    public CivilServantRegistryClient(@Qualifier("civilServantRegistryHttpClient") IHttpClient httpClient,
                                      @Value("${registry-service.getResourceByUidUrl}") String getResourceByUidUrl,
                                      @Value("${registry-service.getResourceByOrgCodeUrl}") String getResourceByOrgCodeUrl) {
        this.httpClient = httpClient;
        this.getResourceByUidUrl = getResourceByUidUrl;
        this.getResourceByOrgCodeUrl = getResourceByOrgCodeUrl;
    }

    @Override
    public Optional<CivilServant> getCivilServantResourceByUid(String uid) {
        log.debug("Getting profile details for civil servant with UID {}", uid);
        String url = String.format(getResourceByUidUrl, uid);
        RequestEntity<Void> request = RequestEntity
                .get(url)
                .build();
        return Optional.ofNullable(httpClient.executeRequest(request, CivilServant.class));
    }

    @Override
    public List<CivilServant> getCivilServantsByOrgCode(String code) {
        log.debug("Getting profile details for civil servants with organisation code {}", code);
        String url = String.format(getResourceByOrgCodeUrl, code);
        RequestEntity<Void> request = RequestEntity
                .get(url)
                .build();
        return httpClient.executeListRequest(request, new ParameterizedTypeReference<>() {
        });
    }
}
