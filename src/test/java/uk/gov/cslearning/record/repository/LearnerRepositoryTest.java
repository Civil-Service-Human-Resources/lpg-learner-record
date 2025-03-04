package uk.gov.cslearning.record.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.domain.Learner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
public class LearnerRepositoryTest extends IntegrationTestBase {

    @Autowired
    private LearnerRepository learnerRepository;

    @Test
    public void shouldSaveLearner() {
        Learner learner = new Learner();
        learner.setUid("test-uuid");
        learner.setLearnerEmail("test@domain.com");
        learnerRepository.save(learner);

        assertThat(learner.getId(), notNullValue());
    }
}
