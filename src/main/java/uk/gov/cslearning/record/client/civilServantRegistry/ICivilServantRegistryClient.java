package uk.gov.cslearning.record.client.civilServantRegistry;

import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;

import java.util.List;
import java.util.Optional;

public interface ICivilServantRegistryClient {

    Optional<CivilServant> getCivilServantResourceByUid(String uid);

    List<OrganisationalUnit> getAllOrganisationalUnits();
    
    List<String> getCivilServantUidsByOrgCode(String code);
}
