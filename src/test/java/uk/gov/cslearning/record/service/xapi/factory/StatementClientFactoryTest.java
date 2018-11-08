package uk.gov.cslearning.record.service.xapi.factory;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatementClientFactoryTest {
    private final String url = "_url";
    private final String username = "_username";
    private final String password = "_password";

    private final StatementClientFactory statementClientFactory = new StatementClientFactory(url, username, password);

    @Test
    public void shouldReturnStatementClientFactory() {


    }
}