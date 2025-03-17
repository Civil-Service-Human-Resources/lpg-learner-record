package uk.gov.cslearning.record.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lpg-ui")
public class LpgUiConfig {

    private String bookingUrlFormat;

    public String getBookingUrl(String courseId, String moduleId) {
        return String.format(this.bookingUrlFormat, courseId, moduleId);
    }
}
