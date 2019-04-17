package uk.gov.cslearning.record.repository;

import org.joda.time.DateTime;

import java.util.List;

public interface CustomStatementsRepository {
    void deleteAllByAge(DateTime dateTime);

    List<?> findAllByAge(DateTime dateTime);


}
