package uk.gov.cslearning.record.domain.factory;

import org.junit.Test;
import uk.gov.cslearning.record.domain.Learner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LearnerFactoryTest {
    private LearnerFactory learnerFactory = new LearnerFactory();

    @Test
    public void shouldReturnLearner() {
        String learnerUid = "learner-uuid";
        String learnerEmail = "test@domain.com";
        Learner learner = learnerFactory.create(learnerUid, learnerEmail);
        assertThat(learner.getUid(), equalTo(learnerUid));
    }
}