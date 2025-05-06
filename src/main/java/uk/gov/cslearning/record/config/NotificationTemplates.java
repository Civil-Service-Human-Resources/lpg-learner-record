package uk.gov.cslearning.record.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.gov.cslearning.record.notifications.dto.NotificationTemplate;

import java.util.Map;

@ConfigurationProperties("notify")
@Data
public class NotificationTemplates {
    private Map<String, String> templates;

    public String getTemplate(NotificationTemplate template) {
        String templateValue = this.templates.get(template.getConfigName());
        if (templateValue == null) {
            throw new RuntimeException(String.format("Email template %s has not been mapped", template));
        }
        return templateValue;
    }
}
