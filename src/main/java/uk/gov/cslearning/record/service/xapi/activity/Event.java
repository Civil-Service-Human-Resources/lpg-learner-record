package uk.gov.cslearning.record.service.xapi.activity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gov.adlnet.xapi.model.Result;
import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.service.xapi.ActivityType;

public class Event extends Activity {

    private static final String PAYMENT_EXTENSION = "http://cslearning.gov.uk/extension/payment";

    private static final String SEPARATOR = ":";

    static {
        Activity.register(Event.class, ActivityType.EVENT);
    }

    Event(Statement statement) {
        super(statement);
    }

    @Override
    public String getCourseId() {
        return getParent(COURSE_ID_PREFIX);
    }

    @Override
    public String getModuleId() {
        return getParent(MODULE_ID_PREFIX);
    }

    @Override
    public String getEventId() {
        return getActivityId();
    }

    public String getPaymentMethod() {
        String payment = getPayment();
        if (payment != null) {
            String[] parts = payment.split(SEPARATOR);
            if (parts.length == 2) {
                return parts[0].trim();
            }
        }
        return null;
    }

    public String getPaymentDetails() {
        String payment = getPayment();
        if (payment != null) {
            String[] parts = payment.split(SEPARATOR);
            if (parts.length == 2) {
                return parts[1].trim();
            }
        }
        return null;
    }
    private String getPayment() {

        Result result = statement.getResult();
        if (result != null) {
            JsonObject extensions = result.getExtensions();
            if (extensions != null) {
                JsonElement element = extensions.get(PAYMENT_EXTENSION);
                if (element != null) {
                    return element.getAsString();
                }
            }
        }
        return null;
    }
}
