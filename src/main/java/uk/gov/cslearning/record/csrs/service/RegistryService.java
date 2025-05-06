package uk.gov.cslearning.record.csrs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.civilServantRegistry.ICivilServantRegistryClient;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegistryService {

    private final ICivilServantRegistryClient civilServantRegistryClient;

    public RegistryService(ICivilServantRegistryClient civilServantRegistryClient) {
        this.civilServantRegistryClient = civilServantRegistryClient;
    }

    public Optional<CivilServant> getCivilServantResourceByUid(String uid) {
        return civilServantRegistryClient.getCivilServantResourceByUid(uid);
    }

    public List<String> getCivilServantsByOrgCode(String code) {
        return civilServantRegistryClient.getCivilServantUidsByOrgCode(code);
    }

    /**
     * @return a list of org hierarchies, starting with the lowest tier and cascading up
     */
    public List<List<String>> getOrganisationCodeHierarchy() {
        List<List<String>> fullHierarchy = new ArrayList<>();
        Map<Integer, OrganisationalUnit> orgMap = civilServantRegistryClient.getAllOrganisationalUnits()
                .stream().collect(Collectors.toMap(OrganisationalUnit::getId, o -> o));
        for (OrganisationalUnit org : orgMap.values()) {
            List<String> hierarchy = new ArrayList<>(List.of(org.getCode()));
            Integer parentId = org.getParentId();
            while (parentId != null) {
                OrganisationalUnit parent = orgMap.get(parentId);
                hierarchy.add(parent.getCode());
                parentId = parent.getParentId();
            }
            fullHierarchy.add(hierarchy);
        }
        return fullHierarchy;
    }

}
