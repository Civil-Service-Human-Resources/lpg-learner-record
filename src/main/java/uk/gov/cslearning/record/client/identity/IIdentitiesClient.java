package uk.gov.cslearning.record.client.identity;


import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.List;
import java.util.Map;

public interface IIdentitiesClient {
    Map<String, Identity> fetchByUids(List<String> uids);
}
