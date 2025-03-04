package uk.gov.cslearning.record.csrs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.client.civilServantRegistry.ICivilServantRegistryClient;
import uk.gov.cslearning.record.csrs.domain.CivilServant;

import java.util.List;
import java.util.Optional;

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

    public List<CivilServant> getCivilServantsByOrgCode(String code) {
        return civilServantRegistryClient.getCivilServantsByOrgCode(code);
    }

}
