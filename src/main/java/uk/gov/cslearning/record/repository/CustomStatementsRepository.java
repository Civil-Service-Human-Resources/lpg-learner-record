package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;

public interface CustomStatementsRepository {
    void deleteAllByAge(DateTime dateTime);
}
