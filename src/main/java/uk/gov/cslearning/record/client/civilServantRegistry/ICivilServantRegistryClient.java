package uk.gov.cslearning.record.client.civilServantRegistry;

import uk.gov.cslearning.record.csrs.domain.CivilServant;

import java.util.List;
import java.util.Optional;

public interface ICivilServantRegistryClient {

    Optional<CivilServant> getCivilServantResourceByUid(String uid);

    List<CivilServant> getCivilServantsByOrgCode(String code);

}
