package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public class RequiredLearningDueMessageParams extends MessageParams implements IMessageParams {

    private final String periodPermission;
    private final String requiredLearning;

    public RequiredLearningDueMessageParams(String recipient, String periodPermission, String requiredLearning) {
        super(recipient);
        this.periodPermission = periodPermission;
        this.requiredLearning = requiredLearning;
    }

    @Override
    public NotificationTemplate getTemplate() {
        return NotificationTemplate.REQUIRED_LEARNING_DUE;
    }

    @Override
    public Map<String, String> getPersonalisation() {
        return Map.of(
                "email address", recipient,
                "periodPermission", periodPermission,
                "requiredLearning", requiredLearning
        );
    }
}
