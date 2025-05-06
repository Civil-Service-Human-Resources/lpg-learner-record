package uk.gov.cslearning.record.service.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.identity.IIdentitiesClient;
import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class IdentitiesService {

    private final IIdentitiesClient iIdentitiesClient;

    private final Integer identityMapMaxBatchSize;

    public IdentitiesService(IIdentitiesClient iIdentitiesClient,
                             @Value("${identity.identityMapMaxBatchSize}") Integer identityMapMaxBatchSize) {
        this.iIdentitiesClient = iIdentitiesClient;
        this.identityMapMaxBatchSize = identityMapMaxBatchSize;
    }

    public Map<String, Identity> fetchByUids(List<String> uids) {
        Map<String, Identity> identitiesMap = new HashMap<>();
        List<List<String>> batchedUids = IntStream.iterate(0, i -> i + identityMapMaxBatchSize)
                .limit((int) Math.ceil((double) uids.size() / identityMapMaxBatchSize))
                .mapToObj(i -> uids.subList(i, Math.min(i + identityMapMaxBatchSize, uids.size())))
                .toList();

        batchedUids.forEach(batch -> {
            Map<String, Identity> identitiesFromUids = iIdentitiesClient.fetchByUids(batch);
            if (identitiesFromUids != null) {
                identitiesMap.putAll(identitiesFromUids);
            }
        });

        return identitiesMap;
    }

    public Map<String, String> getUidToEmailMap(List<String> uids) {
        return fetchByUids(uids).values().stream()
                .collect(Collectors.toMap(Identity::getUid, Identity::getUsername));
    }

    public Optional<Identity> getIdentityByEmailAddress(String emailAddress) {
        return iIdentitiesClient.getIdentityWithEmail(emailAddress);
    }

}
