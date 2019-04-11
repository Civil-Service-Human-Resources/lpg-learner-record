package uk.gov.cslearning.record.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProperties implements Serializable {
    private String uri;

    private String database;
}
