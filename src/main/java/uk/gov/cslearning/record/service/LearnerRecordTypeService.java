package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.record.GetLearnerRecordTypesParams;
import uk.gov.cslearning.record.dto.record.LearnerRecordTypeDto;
import uk.gov.cslearning.record.repository.LearnerRecordTypeRepository;
import uk.gov.cslearning.record.service.factory.LookupValueFactory;

import java.util.List;

@Service
public class LearnerRecordTypeService {

    private final LearnerRecordTypeRepository learnerRecordTypeRepository;
    private final LookupValueFactory factory;


    public LearnerRecordTypeService(LearnerRecordTypeRepository learnerRecordTypeRepository, LookupValueFactory factory) {
        this.learnerRecordTypeRepository = learnerRecordTypeRepository;
        this.factory = factory;
    }

    public List<LearnerRecordTypeDto> getLearnerRecordTypes(GetLearnerRecordTypesParams params) {
        return learnerRecordTypeRepository.findAll()
                .stream().map(type -> factory.createLearnerRecordTypeDto(type, params.isIncludeEventTypes()))
                .toList();
    }
}
