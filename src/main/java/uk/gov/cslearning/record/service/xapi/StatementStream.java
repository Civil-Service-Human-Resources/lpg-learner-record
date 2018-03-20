package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.model.Statement;
import org.codehaus.janino.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.action.Action;
import uk.gov.cslearning.record.service.xapi.activity.Activity;
import uk.gov.cslearning.record.service.xapi.activity.Course;

import java.util.*;

public class StatementStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementStream.class);

    public Collection<CourseRecord> replay(Collection<Statement> statements, GroupId id) {

        Map<String, List<Statement>> groups = new HashMap<>();
        Map<String, CourseRecord> records = new HashMap<>();

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
                String courseId = activity.getCourseId();
                CourseRecord courseRecord = records.get(courseId);
                if (courseRecord == null) {
                    String userId = group.get(0).getActor().getAccount().getName();
                    courseRecord = new CourseRecord(courseId, userId);
                    records.put(courseId, courseRecord);
                }

                if (activity instanceof Course) {
                    for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {
                        replay(statements, moduleRecord);
                    }
                } else {
                    ModuleRecord moduleRecord = new ModuleRecord();
                    moduleRecord.setModuleId(activity.getModuleId());
                    moduleRecord.setEventId(activity.getEventId());

                    replay(statements, moduleRecord);

                    courseRecord.addModuleRecord(moduleRecord);
                }
            }
        }
        return records.values();
    }

    private void replay(Collection<Statement> statements, ModuleRecord moduleRecord) {
        for (Statement statement : statements) {
            Action action = Action.getFor(statement);
            if (action != null) {
                action.replay(moduleRecord);
            } else {
                LOGGER.debug("Unrecognised statement {}", statement.getVerb().getId());
            }
        }
    }

    public interface GroupId {

        String get(Statement statement);
    }
}
