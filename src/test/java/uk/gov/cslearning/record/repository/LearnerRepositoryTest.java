package uk.gov.cslearning.record.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.domain.Learner;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LearnerRepositoryTest {

    @Autowired
    private LearnerRepository learnerRepository;

    @Test
    public void shouldSaveLearner(){
        Learner learner = new Learner(new Long(1));
        learner.setUuid("test-uuid");
        learnerRepository.save(learner);

        assertThat(learner.getId(), notNullValue());
    }
}
