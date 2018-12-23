package uk.gov.cslearning.record.repository;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.ModuleRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ModuleRecordRepositoryTest {

    @Autowired
    private ModuleRecordRepository moduleRecordRepository;

    @Test
    public void shouldReturnListOfRecordsCreatedBetweenTwoDates() {
        LocalDateTime queryStart = LocalDateTime.now().minusDays(2);

        ModuleRecord moduleRecord1 = new ModuleRecord("moduleRecord1");
        moduleRecord1.setCreatedAt(LocalDateTime.now().minusDays(1));

        ModuleRecord moduleRecord2 = new ModuleRecord("moduleRecord2");
        moduleRecord2.setCreatedAt(queryStart);

        ModuleRecord moduleRecord3 = new ModuleRecord("moduleRecord3");
        moduleRecord3.setCreatedAt(LocalDateTime.now().minusDays(3));

        moduleRecordRepository.saveAll(Arrays.asList(moduleRecord1, moduleRecord2, moduleRecord3));

        LocalDateTime end = LocalDateTime.now().minusDays(1).minusMinutes(1);

        List<ModuleRecord> results = moduleRecordRepository.findAllByCreatedAtBetween(queryStart, end);

        assertEquals(1, results.size());
        assertEquals(Collections.singletonList(moduleRecord2), results);
    }
}