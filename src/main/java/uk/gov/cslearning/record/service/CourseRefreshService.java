package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;

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

    @Transactional
    public void refreshCoursesForATimePeriod(LocalDateTime since) {
        try {
            Collection<Statement> statements = xApiService.getStatements(null, null, since);
            StatementStream stream = new StatementStream(learningCatalogueService, registryService);

            Map<String, List<Statement>> userSplit = new HashMap<>();

            log.info("Splitting {} records by user", statements.size());

            for (Statement statement : statements) {
                String userId = statement.getActor().getAccount().getName();
                log.error("Splitting user {}", userId);
                if (userSplit.containsKey(userId)) {
                    userSplit.get(userId).add(statement);
                } else {
                    ArrayList<Statement> newUserStatements = new ArrayList();
                    newUserStatements.add(statement);
                    userSplit.put(userId, new ArrayList<>(newUserStatements));
                }

            }

            log.info("Records split by user, total users {}", userSplit.keySet().size());

            Collection<CourseRecord> updatedCourseRecords = new ArrayList();

            for (String userId : userSplit.keySet()) {
                log.info("Running course refresh for user {}, has {} statements", userId, userSplit.get(userId).size());
                Collection<CourseRecord> existingCourseRecords = transactionTemplate.execute(status -> courseRecordRepository.findByUserId(userId));
                Collection<CourseRecord> userRecords = stream.replay(userSplit.get(userId), statement -> ((Activity) statement.getObject()).getId(), existingCourseRecords);
                log.info("Course refresh complete for user {}, got {} records", userId, userRecords.size());
                updatedCourseRecords.addAll(userRecords);
                log.info("Record count for all users is now {}", updatedCourseRecords.size());
            }

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    courseRecordRepository.saveAll(updatedCourseRecords);
                }
            });
            log.info("Statements saved to DB");
        } catch (IOException e) {
            throw new RuntimeException("Exception retrieving xAPI statements.", e);
        }
    }
}
