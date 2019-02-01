package uk.gov.cslearning.record.service;

import gov.adlnet.xapi.client.BaseClient;
import gov.adlnet.xapi.model.Statement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;

public class CustomStatementClient extends BaseClient {

    private TreeMap<String, String> filters;

    public CustomStatementClient(String uri, String user, String password) throws java.net.MalformedURLException {
        super(uri, user, password);
    }

    public CustomStatementClient(URL uri, String user, String password) throws MalformedURLException {
        super(uri, user, password);
    }

    public CustomStatementClient(String uri, String encodedUsernamePassword) throws MalformedURLException {
        super(uri, encodedUsernamePassword);
    }

    public CustomStatementClient(URL uri, String encodedUsernamePassword) throws MalformedURLException {
        super(uri, encodedUsernamePassword);
    }

    public void deleteStatement(Statement statement) throws IOException {
        this.issueDelete("/statements?statementId=" + statement.getId());
    }
}
