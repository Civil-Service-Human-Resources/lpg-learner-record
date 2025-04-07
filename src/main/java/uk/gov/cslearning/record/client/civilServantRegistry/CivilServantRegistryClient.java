package uk.gov.cslearning.record.client.civilServantRegistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.client.IHttpClient;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.GetPageResponse;
import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class CivilServantRegistryClient implements ICivilServantRegistryClient {

    private final IHttpClient httpClient;
    private final String getResourceByUidUrl;
    private final String getResourceByOrgCodeUrl;
    private final String organisationalUnitsUrl;
    private final Integer getOrganisationsMaxPageSize;
    private final Integer getCSUidsMaxPageSize;

    public CivilServantRegistryClient(@Qualifier("civilServantRegistryHttpClient") IHttpClient httpClient,
                                      @Value("${registry-service.getResourceByUidUrl}") String getResourceByUidUrl,
                                      @Value("${registry-service.getResourceByOrgCodeUrl}") String getResourceByOrgCodeUrl,
                                      @Value("${registry-service.organisationalUnitsUrl}") String organisationalUnitsUrl,
                                      @Value("${registry-service.getOrganisationsMaxPageSize}") Integer getOrganisationsMaxPageSize,
                                      @Value("${registry-service.getCSUidsMaxPageSize}") Integer getCSUidsMaxPageSize) {
        this.httpClient = httpClient;
        this.getResourceByUidUrl = getResourceByUidUrl;
        this.getResourceByOrgCodeUrl = getResourceByOrgCodeUrl;
        this.organisationalUnitsUrl = organisationalUnitsUrl;
        this.getOrganisationsMaxPageSize = getOrganisationsMaxPageSize;
        this.getCSUidsMaxPageSize = getCSUidsMaxPageSize;
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

    private GetPageResponse<OrganisationalUnit> getOrganisations(Integer size, Integer page) {
        String url = organisationalUnitsUrl + String.format("?size=%s&page=%s", size, page);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        GetPageResponse<OrganisationalUnit> initialResponse = getOrganisations(1, 0);
        if (initialResponse.getTotalElements() >= 1) {
            List<CompletableFuture<List<OrganisationalUnit>>> futures =
                    IntStream.range(0, (int) Math.ceil((double) initialResponse.getTotalElements() / getOrganisationsMaxPageSize))
                            .boxed()
                            .map(i -> CompletableFuture.supplyAsync(() -> getOrganisations(getOrganisationsMaxPageSize, i).getContent())).toList();

            organisationalUnits = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(i -> futures.stream().flatMap(listCompletableFuture -> listCompletableFuture.join().stream()).collect(toList())).join();

        }
        return organisationalUnits;
    }

    private GetPageResponse<String> getCivilServantUidsByOrgCode(String orgCode, Integer size, Integer page) {
        String url = String.format("%s/%s?size=%s&page=%s", getResourceByOrgCodeUrl, orgCode, size, page);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public List<String> getCivilServantUidsByOrgCode(String code) {
        List<String> uids = new ArrayList<>();
        GetPageResponse<String> initialResponse = getCivilServantUidsByOrgCode(code, 1, 0);
        if (initialResponse.getTotalElements() >= 1) {
            List<CompletableFuture<List<String>>> futures =
                    IntStream.range(0, (int) Math.ceil((double) initialResponse.getTotalElements() / getCSUidsMaxPageSize))
                            .boxed()
                            .map(i -> CompletableFuture.supplyAsync(() -> getCivilServantUidsByOrgCode(code, getCSUidsMaxPageSize, i).getContent())).toList();

            uids = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(i -> futures.stream().flatMap(listCompletableFuture -> listCompletableFuture.join().stream()).collect(toList())).join();

        }
        return uids;
    }
}
