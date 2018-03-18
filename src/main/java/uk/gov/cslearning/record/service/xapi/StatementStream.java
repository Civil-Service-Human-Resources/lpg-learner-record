package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.action.Action;
import uk.gov.cslearning.record.service.xapi.activity.Activity;

import java.util.*;

public class StatementStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementStream.class);

    public Collection<Record> replay(Collection<Statement> statements, GroupId id) {

        Collection<Record> records = new ArrayList<>();
        Map<String, List<Statement>> groups = new HashMap<>();

        for (Statement statement : statements) {
            String groupId = id.get(statement);
            if (!groups.containsKey(groupId)) {
                groups.put(groupId, new ArrayList<>());
            }
            groups.get(groupId).add(statement);
        }

        for (Map.Entry<String, List<Statement>> entry : groups.entrySet()) {

            List<Statement> group = entry.getValue();
            group.sort(Comparator.comparing(Statement::getTimestamp));

            Activity activity = Activity.getFor(group.get(0));

            if (activity != null) {
                Record record = new Record();
                record.setCourseId(activity.getCourseId());
                record.setModuleId(activity.getModuleId());
                record.setEventId(activity.getEventId());
                record.setUserId(group.get(0).getActor().getAccount().getName());

                for (Statement statement : group) {
                    Action action = Action.getFor(statement);
                    if (action != null) {
                        action.replay(record);
                    } else {
                        LOGGER.debug("Unrecognised statement {}", statement.getVerb().getId());
                    }
                }
                records.add(record);
            }
        }
        return records;
    }

    public interface GroupId {

        String get(Statement statement);
    }
}
