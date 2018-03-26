package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.service.xapi.action.Action;
import uk.gov.cslearning.record.service.xapi.activity.Activity;
import uk.gov.cslearning.record.service.xapi.activity.Course;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class StatementStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementStream.class);

    public Collection<CourseRecord> replay(Collection<Statement> statements, GroupId id) {

        Map<String, List<Statement>> groups = new HashMap<>();
        Map<String, CourseRecord> records = new HashMap<>();

        List<Statement> sortedStatements = new ArrayList<>(statements);
        sortedStatements.sort(Comparator.comparing(Statement::getTimestamp));

        for (Statement statement : statements) {
            String groupId = id.get(statement);
            if (!groups.containsKey(groupId)) {
                groups.put(groupId, new ArrayList<>());
            }
            groups.get(groupId).add(statement);

            List<String> relatedIds = getParents(statement);
            for (String relatedId : relatedIds) {
                if (groups.containsKey(relatedId)) {
                    groups.get(groupId).addAll(groups.get(relatedId));
                }
            }
        }

        for (Map.Entry<String, List<Statement>> entry : groups.entrySet()) {

            List<Statement> group = entry.getValue();
            group.sort(Comparator.comparing(Statement::getTimestamp));

            for (Statement statement : group) {
                Activity activity = Activity.getFor(statement);

                if (activity != null) {
                    String courseId = activity.getCourseId();
                    if (courseId == null) {
                        LOGGER.info("Ignoring statement with no course ID {}", statement);
                        continue;
                    }
                    CourseRecord courseRecord = records.get(courseId);
                    if (courseRecord == null) {
                        String userId = group.get(0).getActor().getAccount().getName();
                        if (userId == null) {
                            LOGGER.info("Ignoring statement with no user ID {}", statement);
                            continue;
                        }
                        courseRecord = new CourseRecord(courseId, userId);
                        records.put(courseId, courseRecord);
                    }

                    if (activity instanceof Course) {
                        replay(statement, courseRecord, null);
                        for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {
                            replay(statement, courseRecord, moduleRecord);
                        }
                    } else {
                        String moduleId = activity.getModuleId();
                        if (moduleId == null) {
                            LOGGER.info("Ignoring statement with no module ID {}", statement);
                            continue;
                        }
                        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
                        if (moduleRecord == null) {
                            moduleRecord = new ModuleRecord(moduleId);
                            courseRecord.addModuleRecord(moduleRecord);
                        }

                        moduleRecord.setEventId(activity.getEventId());
                        replay(statement, courseRecord, moduleRecord);
                    }
                }
            }
        }
        return records.values();
    }

    private List<String> getParents(Statement statement) {
        if (statement.getContext() != null
                && statement.getContext().getContextActivities() != null) {
            List<gov.adlnet.xapi.model.Activity> parents = statement.getContext().getContextActivities().getParent();
            if (parents != null) {
                return parents.stream().map(gov.adlnet.xapi.model.Activity::getId).collect(toList());
            }
        }
        return emptyList();
    }

    private void replay(Statement statement, CourseRecord courseRecord, ModuleRecord moduleRecord) {
        Action action = Action.getFor(statement);
        if (action != null) {
            action.replay(courseRecord, moduleRecord);
        } else {
            LOGGER.debug("Unrecognised statement {}", statement.getVerb().getId());
        }
    }

    public interface GroupId {

        String get(Statement statement);
    }
}
