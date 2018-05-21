package uk.gov.cslearning.record.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegistryServiceTest {

    @InjectMocks
    private RegistryService registryService;

    @Test
    public void getProfileById() {
        registryService.getCivilServantByUid("3c706a70-3fff-4e7b-ae7f-102c1d46f569");
    }
}