package uk.gov.cslearning.record.service.xapi;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import gov.adlnet.xapi.model.Statement;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.service.catalogue.Audience;
import uk.gov.cslearning.record.service.catalogue.Event;
import uk.gov.cslearning.record.service.catalogue.LearningCatalogueService;
import uk.gov.cslearning.record.service.catalogue.Module;
import uk.gov.cslearning.record.service.xapi.action.Action;
import uk.gov.cslearning.record.service.xapi.activity.Activity;
import uk.gov.cslearning.record.service.xapi.activity.Course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatementStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementStream.class);

    private LearningCatalogueService learningCatalogueService;
    private RegistryService registryService;

    public StatementStream(LearningCatalogueService learningCatalogueService, RegistryService registryService) {
        checkArgument(learningCatalogueService != null);
        this.learningCatalogueService = learningCatalogueService;
        this.registryService = registryService;
    }

    public Collection<CourseRecord> replay(Collection<Statement> statements, GroupId id) {
        return replay(statements, id, Collections.emptySet());
    }

    public Collection<CourseRecord> replay(Collection<Statement> statements, GroupId id, Collection<CourseRecord> existingCourseRecords) {
        Map<String, List<Statement>> groups = new HashMap<>();
        Map<String, CourseRecord> records = existingCourseRecords.stream()
                .collect(toMap(CourseRecord::getCourseId, c -> c));

        Map<String, CourseRecord> updatedRecords = new HashMap<>();

        List<Statement> sortedStatements = new ArrayList<>(statements);
        sortedStatements.sort(Comparator.comparing(Statement::getTimestamp));

        for (Statement statement : sortedStatements) {
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
                    uk.gov.cslearning.record.service.catalogue.Course catalogueCourse
                            = learningCatalogueService.getCachedCourse(courseId);

                    if (catalogueCourse == null) {
                        LOGGER.info("Ignoring statement with no catalogue course, courseId: {}", courseId);
                        continue;
                    }

                    if (courseRecord == null) {
                        String userId = group.get(0).getActor().getAccount().getName();
                        if (userId == null) {
                            LOGGER.info("Ignoring statement with no user ID {}", statement);
                            continue;
                        }
                        courseRecord = new CourseRecord(courseId, userId);
                        courseRecord.setCourseTitle(catalogueCourse.getTitle());
                        courseRecord.setRequired(false);

                        Optional<CivilServant> civilServant = registryService.getCivilServantByUid(userId);

                        if (civilServant.isPresent() && isCourseRequired(catalogueCourse, civilServant.get())) {
                            courseRecord.setRequired(true);
                        }
                        records.put(courseId, courseRecord);
                    }
                    updatedRecords.put(courseId, courseRecord);

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
                        Module catalogueModule = catalogueCourse.getModule(moduleId);

                        if (catalogueModule == null) {
                            LOGGER.info("Ignoring statement with no catalogue module, courseId: {}, moduleId: {}",
                                    courseId, moduleId);
                            continue;
                        }

                        if (moduleRecord == null) {
                            moduleRecord = new ModuleRecord(moduleId);
                            moduleRecord.setCreatedAt(LocalDateTime.parse(statement.getTimestamp(), XApiService.DATE_FORMATTER));

                            moduleRecord.setModuleTitle(catalogueModule.getTitle());
                            moduleRecord.setCost(catalogueModule.getCost());
                            moduleRecord.setOptional(catalogueModule.isOptional());
                            moduleRecord.setModuleType(catalogueModule.getModuleType());
                            moduleRecord.setDuration(catalogueModule.getDuration());

                            courseRecord.addModuleRecord(moduleRecord);
                        }

                        String eventId = activity.getEventId();
                        if (eventId != null) {
                            moduleRecord.setEventId(eventId);

                            Event catalogueEvent = catalogueModule.getEvent(eventId);
                            if (catalogueEvent == null) {
                                LOGGER.info("Ignoring statement with no catalogue event, courseId: {}, moduleId: {}, eventId: {}",
                                        courseId, moduleId, eventId);
                                continue;
                            }

                            if (!catalogueEvent.getDateRanges().isEmpty()) {
                                moduleRecord.setEventDate(catalogueEvent.getDateRanges().get(0).getDate());
                            }
                        }

                        replay(statement, courseRecord, moduleRecord);

                        if (checkComplete(courseRecord, catalogueCourse)) {
                            courseRecord.setState(State.COMPLETED);
                        }
                    }
                }
            }
        }
        return updatedRecords.values();
    }

    private boolean checkComplete(CourseRecord courseRecord, uk.gov.cslearning.record.service.catalogue.Course catalogueCourse) {

        boolean hasRequired = catalogueCourse.getModules().stream()
                .anyMatch(module -> !module.isOptional());

        for (Module module : catalogueCourse.getModules()) {
            if (!hasRequired || !module.isOptional()) {
                ModuleRecord record = courseRecord.getModuleRecord(module.getId());
                if (record == null || record.getState() != State.COMPLETED) {
                    return false;
                }
            }
        }
        return true;
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
        courseRecord.setLastUpdated(LocalDateTime.parse(statement.getTimestamp(), XApiService.DATE_FORMATTER));

        if (moduleRecord != null) {
            moduleRecord.setUpdatedAt(courseRecord.getLastUpdated());
        }
    }

    private boolean isCourseRequired(uk.gov.cslearning.record.service.catalogue.Course catalogueCourse, CivilServant civilServant) {
        return catalogueCourse.getAudiences()
            .stream()
            .anyMatch(audience -> audience.getType().equals(Audience.Type.REQUIRED_LEARNING)
                && civilServant.getOrganisationalUnit() != null
                && audience.getDepartments().contains(civilServant.getOrganisationalUnit().getCode()));
    }

    public interface GroupId {

        String get(Statement statement);
    }
}
