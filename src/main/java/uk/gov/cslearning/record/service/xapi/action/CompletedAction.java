package uk.gov.cslearning.record.service.xapi.action;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.xapi.ActivityType;
import uk.gov.cslearning.record.service.xapi.Verb;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.time.LocalDateTime;

public class CompletedAction extends Action {

    static {
        Action.register(CompletedAction.class, ActivityType.ELEARNING, Verb.COMPLETED);
        Action.register(CompletedAction.class, ActivityType.VIDEO, Verb.COMPLETED);
        Action.register(CompletedAction.class, ActivityType.LINK, Verb.EXPERIENCED);
    }

    CompletedAction(Statement statement) {
        super(statement);
    }

    @Override
    public void replay(CourseRecord courseRecord, ModuleRecord moduleRecord) {
        courseRecord.setState(null);
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(LocalDateTime.parse(statement.getTimestamp(), XApiService.DATE_FORMATTER));
    }
}
