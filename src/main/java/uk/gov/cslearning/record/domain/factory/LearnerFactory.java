package uk.gov.cslearning.record.domain.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.domain.Learner;

@Component
public class LearnerFactory {
    public Learner create(String uuid, String learnerEmail) {
        Learner learner = new Learner();
        learner.setUid(uuid);
        learner.setLearnerEmail(learnerEmail);

        return learner;
    }
}
