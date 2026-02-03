package uk.gov.cslearning.record.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.notifications.dto.IMessageParams;
import uk.gov.cslearning.record.notifications.dto.RequiredLearningDueMessageParams;

import java.util.List;

@Service
public class MessageService {

    public IMessageParams createIncompleteCoursesMessage(String email, List<String> requiredLearningTitles, String period) {
        StringBuilder requiredLearningStr = new StringBuilder();
        for (String title : requiredLearningTitles) {
            requiredLearningStr
                    .append(title)
                    .append("\n");
        }
        return new RequiredLearningDueMessageParams(email, period, requiredLearningStr.toString());
    }

}
