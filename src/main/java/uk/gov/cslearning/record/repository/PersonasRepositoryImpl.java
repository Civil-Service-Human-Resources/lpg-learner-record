package uk.gov.cslearning.record.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import uk.gov.cslearning.record.domain.collections.Personas;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class PersonasRepositoryImpl implements CustomStatementsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PersonasRepositoryImpl() {
    }

    public Optional<Personas> findByUid(String uid) {
        ObjectId objID = new ObjectId(uid);

        Query query = new Query(where("personaIdentifiers").is(objID));
        return Optional.ofNullable(mongoTemplate.findOne(query, Personas.class));
    }

    @Override
    public void deleteAllByAge(DateTime dateTime) {
        Query query = new Query(where("updatedAt").lte(dateTime));
        mongoTemplate.remove(query, Personas.class);
    }

    @Override
    public List<Personas> findAllByAge(DateTime dateTime) {
        Query query = new Query(where("updatedAt").lte(dateTime));
        return mongoTemplate.find(query, Personas.class);
    }
}