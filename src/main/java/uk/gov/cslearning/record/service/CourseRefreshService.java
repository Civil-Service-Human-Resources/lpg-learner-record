package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class CourseRefreshService {

    private final TransactionTemplate transactionTemplate;
    private final CourseRecordRepository courseRecordRepository;
    private final LearningCatalogueService learningCatalogueService;
    private final RegistryService registryService;
    private final XApiService xApiService;

    @Autowired
    public CourseRefreshService(PlatformTransactionManager transactionManager,
                                CourseRecordRepository courseRecordRepository,
                                LearningCatalogueService learningCatalogueService,
                                RegistryService registryService,
                                XApiService xApiService) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.courseRecordRepository = courseRecordRepository;
        this.learningCatalogueService = learningCatalogueService;
        this.registryService = registryService;
        this.xApiService = xApiService;
    }

    public int refreshCoursesForATimePeriod(LocalDateTime since) {
        try {
            Collection<Statement> statements = xApiService.getStatements(null, null, since);
            StatementStream stream = new StatementStream(learningCatalogueService, registryService);
            Collection<CourseRecord> updatedCourseRecords = stream.replay(statements, statement -> ((Activity) statement.getObject()).getId());

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    courseRecordRepository.saveAll(updatedCourseRecords);
                }
            });
            return updatedCourseRecords.size();
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
