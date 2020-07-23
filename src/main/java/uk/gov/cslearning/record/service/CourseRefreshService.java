package uk.gov.cslearning.record.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CourseRefreshService {
    private final CourseRecordRepository courseRecordRepository;
    private final LearningCatalogueService learningCatalogueService;
    private final XApiService xApiService;

    @Autowired
    public CourseRefreshService(CourseRecordRepository courseRecordRepository,
            LearningCatalogueService learningCatalogueService,
            XApiService xApiService) {
        this.courseRecordRepository = courseRecordRepository;
        this.learningCatalogueService = learningCatalogueService;
        this.xApiService = xApiService;
    }

    @Transactional
    public void refreshCoursesForATimePeriod(LocalDateTime since) {
        try {
            Collection<Statement> statements = xApiService.getStatements(null, null, since);
            StatementStream stream = new StatementStream(learningCatalogueService);
            Collection<CourseRecord> updatedCourseRecords = stream.replay(statements,  statement -> ((Activity) statement.getObject()).getId());

            courseRecordRepository.saveAll(updatedCourseRecords);
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
