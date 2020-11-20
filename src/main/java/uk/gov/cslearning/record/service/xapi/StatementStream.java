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
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;
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

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
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

        log.info("Mapping statement collection");
        Map<String, CourseRecord> records = existingCourseRecords.stream()
                .collect(toMap(CourseRecord::getCourseId, c -> c));
        log.info("Mapping done");

        Map<String, CourseRecord> updatedRecords = new HashMap<>();

        List<Statement> sortedStatements = new ArrayList<>(statements);
        log.info("Sorting statements");
        sortedStatements.sort(Comparator.comparing(Statement::getTimestamp));
        log.info("Statements sorted");

        log.info("Grouping statements");
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
        log.info("Statements grouped");

        log.info("Processing statement groups");
        for (Map.Entry<String, List<Statement>> entry : groups.entrySet()) {

            log.info("Processing group {}", entry.getKey());

            List<Statement> group = entry.getValue();

            log.info("Sorting group {}", entry.getKey());
            group.sort(Comparator.comparing(Statement::getTimestamp));
            log.info("Group {} sorted", entry.getKey());

            log.info("Processing group {} statements", entry.getKey());
            for (Statement statement : group) {
                log.info("Processing statement {} in group {}", statement.getId(), entry.getKey());
                log.info("Data filling, statement {} in group {}", statement.getId(), entry.getKey());
                Activity activity = Activity.getFor(statement);

                if (activity != null) {
                    String courseId = activity.getCourseId();
                    if (courseId == null) {
                        LOGGER.info("Ignoring statement with no course ID {}", statement);
                        continue;
                    }
                    log.info("Statement has course {}", courseId);

                    log.info("Getting course record from existing data (Should be empty collection)");
                    CourseRecord courseRecord = records.get(courseId);
                    log.info("Retrieved from existing data set (Should be empty and instantaneous)");
                    log.info("Getting course {} info from catalogue", courseId);
                    uk.gov.cslearning.record.service.catalogue.Course catalogueCourse
                            = learningCatalogueService.getCachedCourse(courseId);
                    log.info("Course {} retrieved from catalogue", courseId);

                    if (catalogueCourse == null) {
                        LOGGER.info("Ignoring statement with no catalogue course, courseId: {}", courseId);
                        continue;
                    }

                    if (courseRecord == null) {
                        log.info("Record not found in existing data, creating (This should always happen)");
                        String userId = group.get(0).getActor().getAccount().getName();
                        if (userId == null) {
                            LOGGER.info("Ignoring statement with no user ID {}", statement);
                            continue;
                        }
                        courseRecord = new CourseRecord(courseId, userId);
                        courseRecord.setCourseTitle(catalogueCourse.getTitle());
                        courseRecord.setRequired(false);

                        log.info("Getting user {} info for course from CSRS", userId);
                        Optional<CivilServant> civilServant = registryService.getCivilServantByUid(userId);
                        log.info("User {} info retrieved", userId);

                        log.info("Checking if course {} is mandatory for user {}", courseId, userId);
                        if (civilServant.isPresent() && isCourseRequired(catalogueCourse, civilServant.get())) {
                            // TODO: Might be a bug here, if course is mandatory for user x but not user y, if user x is processed first user y will be processed after and set required to false, mandatory seems to be decided here while considering a specific user for everyone
                            log.info("Course {} is mandatory for user {}", courseId, userId);
                            courseRecord.setRequired(true);
                        }
                        //TODO: Might be a bug part 2, this rights the record back to the list of course with the mandatory flag set, this block is only entered if a course has not been seen before
                        log.info("\"Cache\" course info");
                        records.put(courseId, courseRecord);
                    }

                    log.info("Adding course record {} to updated set", courseId);
                    updatedRecords.put(courseId, courseRecord);
                    log.info("Added {}", courseId);

                    log.info("Data filling complete, statement {} in group {}", statement.getId(), entry.getKey());

                    log.info("Action processing, statement {} in group {}", statement.getId(), entry.getKey());

                    if (activity instanceof Course) {
                        log.info("Statement is a course, doing course updates");
                        replay(statement, courseRecord, null);
                        for (ModuleRecord moduleRecord : courseRecord.getModuleRecords()) {
                            replay(statement, courseRecord, moduleRecord);
                        }
                        log.info("Completed statement course updates");
                    } else {
                        log.info("Statement is a module, doing module updates");
                        String moduleId = activity.getModuleId();
                        if (moduleId == null) {
                            LOGGER.info("Ignoring statement with no module ID {}", statement);
                            continue;
                        }

                        log.info("Getting existing module record");
                        ModuleRecord moduleRecord = courseRecord.getModuleRecord(moduleId);
                        Module catalogueModule = catalogueCourse.getModule(moduleId);

                        if (catalogueModule == null) {
                            LOGGER.info("Ignoring statement with no catalogue module, courseId: {}, moduleId: {}",
                                    courseId, moduleId);
                            continue;
                        }

                        if (moduleRecord == null) {
                            log.info("No existing module record, creating");
                            moduleRecord = new ModuleRecord(moduleId);
                            moduleRecord.setCreatedAt(LocalDateTime.parse(statement.getTimestamp(), XApiService.DATE_FORMATTER));

                            moduleRecord.setModuleTitle(catalogueModule.getTitle());
                            moduleRecord.setCost(catalogueModule.getCost());
                            moduleRecord.setOptional(catalogueModule.isOptional());
                            moduleRecord.setModuleType(catalogueModule.getModuleType());
                            moduleRecord.setDuration(catalogueModule.getDuration());

                            courseRecord.addModuleRecord(moduleRecord);
                            log.info("Module record created");
                        }

                        String eventId = activity.getEventId();
                        if (eventId != null) {
                            log.info("Module has an event, getting event info");
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
                            log.info("Filled event info");
                        }

                        replay(statement, courseRecord, moduleRecord);

                        log.info("Checking if updated infomation completes course");
                        if (checkComplete(courseRecord, catalogueCourse)) {
                            log.info("Course is completed, updating");
                            courseRecord.setState(State.COMPLETED);
                        }
                    }
                }
            }
            log.info("Processed group {} statements", entry.getKey());
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
        if (civilServant.getOrganisationalUnit() != null) {
            List<String> organisationalUnitCodes = registryService.getOrganisationalUnitByCode(civilServant.getOrganisationalUnit().getCode())
                .stream()
                .map(OrganisationalUnit::getCode)
                .collect(toList());
            return catalogueCourse.getAudiences()
                    .stream()
                    .anyMatch(audience -> audience.getType() != null &&
                        audience.getType().equals(Audience.Type.REQUIRED_LEARNING) &&
                        CollectionUtils.containsAny(audience.getDepartments(), organisationalUnitCodes));
        } else {
            return false;
        }
    }

    public interface GroupId {

        String get(Statement statement);
    }
}
