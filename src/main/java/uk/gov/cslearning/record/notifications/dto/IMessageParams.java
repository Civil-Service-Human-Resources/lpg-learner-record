package uk.gov.cslearning.record.notifications.dto;

import java.util.Map;

public interface IMessageParams {

    String getRecipient();

    NotificationTemplate getTemplate();

    Map<String, String> getPersonalisation();

}
