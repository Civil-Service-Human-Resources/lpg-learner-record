package uk.gov.cslearning.record.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "uk.gov.cslearning.record.repository")
public class MongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties mongoProperties;

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.socketTimeout(mongoProperties.getSocketTimeoutInMilliseconds());
        options.connectTimeout(mongoProperties.getConnectionTimeoutInMilliseconds());
        options.maxConnectionIdleTime(mongoProperties.getMaxConnectionIdleTimeInMilliseconds());

        return new MongoClient(new MongoClientURI(mongoProperties.getUri(), options));
    }
}
