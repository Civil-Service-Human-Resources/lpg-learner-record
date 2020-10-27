package uk.gov.cslearning.record.service;

import static com.mongodb.client.model.Filters.gte;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Actor;
import gov.adlnet.xapi.model.IStatementObject;
import gov.adlnet.xapi.model.Statement;
import gov.adlnet.xapi.model.adapters.ActorAdapter;
import gov.adlnet.xapi.model.adapters.StatementObjectAdapter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.record.config.MongoProperties;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.xapi.StatementStream;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
@Slf4j
public class CourseRefreshService {
    private static final Gson GSON = prepareDecoder();
    private static final String MONGO_DOCUMENT_NAME = "statements";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String STATEMENT_FIELD = "statements";

    private final TransactionTemplate transactionTemplate;
    private final CourseRecordRepository courseRecordRepository;
    private final LearningCatalogueService learningCatalogueService;
    private final RegistryService registryService;
    private final MongoProperties mongoProperties;
    private final MongoClient mongoClient;

    @Value("${notifications.lr-refresh-job-statement-buffer}")
    private int lrRefreshJobStatementBuffer;

    @Autowired
    public CourseRefreshService(PlatformTransactionManager transactionManager,
                                CourseRecordRepository courseRecordRepository,
                                LearningCatalogueService learningCatalogueService,
                                RegistryService registryService,
                                MongoProperties mongoProperties,
                                MongoClient mongoClient) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.courseRecordRepository = courseRecordRepository;
        this.learningCatalogueService = learningCatalogueService;
        this.registryService = registryService;
        this.mongoProperties = mongoProperties;
        this.mongoClient = mongoClient;
    }

    public int refreshCoursesForATimePeriod(LocalDateTime since) {
        log.info("Getting xapi refresh statements since {}", since);
        Collection<Statement> statements = fetchStatementsBySinceDate(since);

        StatementStream stream = new StatementStream(learningCatalogueService, registryService);

        Map<String, List<Statement>> userSplit = new HashMap<>();

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
    }

    private List<Statement> fetchStatementsBySinceDate(LocalDateTime since) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoProperties.getDatabase());
        MongoCollection<Document> documents = mongoDatabase
            .getCollection(MONGO_DOCUMENT_NAME);
        int lastPageNumber = calculateLastPage(documents);
        List<Statement> statements = new ArrayList<>();

        for (int pageNum = 0; pageNum < lastPageNumber; pageNum++) {
            statements.addAll(documents.find(gte(TIMESTAMP_FIELD, Date.from(since.atZone(ZoneId.systemDefault()).toInstant())))
                .skip(lrRefreshJobStatementBuffer * pageNum)
                .limit(lrRefreshJobStatementBuffer)
                .into(new ArrayList<>())
                .stream()
                .map(document -> mapStatement((Document) document.get(STATEMENT_FIELD)))
                .collect(Collectors.toList()));
        }

        return statements;
    }

    private int calculateLastPage(MongoCollection<Document> documents) {
       return (int) Math.ceil((double) documents.count() / lrRefreshJobStatementBuffer);
    }

    private Statement mapStatement(Document statementDocument) {
        return GSON.fromJson(statementDocument.toJson(), Statement.class);
    }

    private static Gson prepareDecoder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Actor.class, new ActorAdapter());
        builder.registerTypeAdapter(IStatementObject.class,
            new StatementObjectAdapter());
        return builder.create();
    }
}
