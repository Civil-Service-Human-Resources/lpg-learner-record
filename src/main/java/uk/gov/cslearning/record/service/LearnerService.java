package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.factory.LearnerFactory;
import uk.gov.cslearning.record.repository.LearnerRepository;

@Service
public class LearnerService {

    private final LearnerFactory learnerFactory;
    private final LearnerRepository learnerRepository;

    public LearnerService(LearnerFactory learnerFactory, LearnerRepository learnerRepository) {
        this.learnerFactory = learnerFactory;
        this.learnerRepository = learnerRepository;
    }

}
