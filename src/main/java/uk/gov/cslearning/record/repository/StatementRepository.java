package uk.gov.cslearning.record.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.record.domain.Statement;

import java.util.Optional;

@Repository
public interface StatementRepository extends MongoRepository<Statement, String> {

    Optional<Statement> getStatementById(String id);
}
