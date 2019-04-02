package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;
import uk.gov.cslearning.record.service.xapi.activity.Activity;
import uk.gov.cslearning.record.service.xapi.activity.Event;

public class RegisteredAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisteredAction.class);

    private static final String CALL_OFF_PREFIX = "Call off";

    static {
        Action.register(RegisteredAction.class, ActivityType.EVENT, Verb.REGISTERED);
    }

    RegisteredAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        if (courseRecord.getState() == null || courseRecord.getState() != State.IN_PROGRESS) {
            courseRecord.setState(State.REGISTERED);
        }
        moduleRecord.setState(State.REGISTERED);
        moduleRecord.setResult(null);
        moduleRecord.setScore(null);
        moduleRecord.setCompletionDate(null);

        Activity activity = Activity.getFor(statement);
        if (activity instanceof Event) {
            Event event = (Event) activity;
            moduleRecord.setPaymentMethod(event.getPaymentMethod());
            moduleRecord.setPaymentDetails(event.getPaymentDetails());

            if (event.getPaymentDetails() != null && event.getPaymentDetails().startsWith(CALL_OFF_PREFIX)) {
                if (courseRecord.getState() == State.REGISTERED) {
                    courseRecord.setState(State.APPROVED);
                }
            }
        } else {
            LOGGER.warn("Registered action taken on module that is not an event. Course ID: {}, module ID: {}",
                    courseRecord.getCourseId(), moduleRecord.getModuleId());
        }
    }
}
