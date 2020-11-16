package uk.gov.cslearning.record.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.XApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Slf4j
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
            log.info("Getting xapi refresh statements since {}", since);
            Collection<Statement> statements = xApiService.getStatements(null, null, since);

            StatementStream stream = new StatementStream(learningCatalogueService, registryService);

            Map<String, List<Statement>> userSplit = new HashMap<>();

            for (Statement statement : statements) {
                String userId = statement.getActor().getAccount().getName();
                log.debug("Splitting user {}", userId);
                if (userSplit.containsKey(userId)) {
                    userSplit.get(userId).add(statement);
                } else {
                    ArrayList<Statement> newUserStatements = new ArrayList();
                    newUserStatements.add(statement);
                    userSplit.put(userId, new ArrayList<>(newUserStatements));
                }
            }

            Collection<CourseRecord> updatedCourseRecords = new ArrayList();

            for (String userId : userSplit.keySet()) {
                Collection<CourseRecord> existingCourseRecords = transactionTemplate.execute(status -> courseRecordRepository.findByUserId(userId));
                Collection<CourseRecord> userRecords = stream.replay(userSplit.get(userId), statement -> ((Activity) statement.getObject()).getId(), existingCourseRecords);
                updatedCourseRecords.addAll(userRecords);
            }

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    courseRecordRepository.saveAll(updatedCourseRecords);
                }
            });
            log.info("Statements saved to DB");
            return updatedCourseRecords.size();
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
