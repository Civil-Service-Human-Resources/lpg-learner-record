package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.client.StatementClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.service.CustomStatementClient;
import uk.gov.cslearning.record.service.xapi.exception.StatementClientCreationException;

import java.net.MalformedURLException;

@Component
public class StatementClientFactory {
    private String url;
    private String username;
    private String password;

    public StatementClientFactory(
            @Value("${xapi.url}") String url,
            @Value("${xapi.username}") String username,
            @Value("${xapi.password}") String password
    ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public StatementClient create() {
        try {
            return new StatementClient(url, username, password);
        } catch (MalformedURLException e) {
            throw new StatementClientCreationException(e);
        }
    }

    public CustomStatementClient createCustom() {
        try {
            return new CustomStatementClient(url, username, password);
        } catch (MalformedURLException e) {
            throw new StatementClientCreationException(e);
        }
    }
}
