package uk.gov.cslearning.record.client.civilServantRegistry;

import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;

import java.util.List;

public interface ICivilServantRegistryClient {

    List<OrganisationalUnit> getAllOrganisationalUnits();

    List<String> getCivilServantUidsByOrgCode(String code);
}
