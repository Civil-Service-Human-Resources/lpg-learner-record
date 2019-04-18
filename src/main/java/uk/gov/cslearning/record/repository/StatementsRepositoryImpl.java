package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import uk.gov.cslearning.record.domain.collections.Statements;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class StatementsRepositoryImpl implements CustomStatementsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StatementsRepositoryImpl() {
    }

    @Override
    public void deleteAllByAge(DateTime dateTime) {
        Query query = new Query(where("timestamp").lte(dateTime));
        mongoTemplate.remove(query, Statements.class);
    }
}