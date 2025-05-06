package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.api.FetchCourseRecordParams;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
import uk.gov.cslearning.record.exception.ResourceExists.CourseRecordAlreadyExistsException;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.util.IUtilService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;
    private final ModuleRecordService moduleRecordService;
    private final IUtilService utilService;

    public CourseRecord updateCourseRecord(CourseRecord input) {
        String userId = input.getUserId();
        String courseId = input.getCourseId();
        log.debug(String.format("Updating course record for course %s and user %s", courseId, userId));
        CourseRecord courseRecord = courseRecordRepository.getCourseRecord(userId, courseId).orElseThrow(() -> new CourseRecordNotFoundException(userId, courseId));
        LocalDateTime updated = utilService.getNowDateTime();
        input.setLastUpdated(updated);
        courseRecord.update(input);
        List<ModuleRecord> updatedModules = new ArrayList<>();
        for (ModuleRecord mr : input.getModuleRecords()) {
            log.debug(String.format("Processing module %s for user %s", mr.getModuleId(), userId));
            Long moduleRecordId = mr.getId();
            if (moduleRecordId == null) {
                log.debug("No module record ID found; creating module");
                mr = moduleRecordService.createModuleRecord(mr, courseRecord, updated);
                courseRecord.addModuleRecord(mr);
            } else {
                log.debug(String.format("Module record ID %s found; updating module", moduleRecordId));
                mr = moduleRecordService.updateModuleRecord(moduleRecordId, mr, updated);
            }
            updatedModules.add(mr);
        }
        log.debug("Saving course record");
        courseRecordRepository.saveAndFlush(courseRecord);
        input.setModuleRecords(updatedModules);
        return input;
    }

    public List<CourseRecord> fetchCourseRecords(FetchCourseRecordParams fetchCourseRecordParams) {
        log.info(String.format("Fetching all course records for user '%s' and course IDs '%s'", fetchCourseRecordParams.getUserIds(), fetchCourseRecordParams.getCourseIds()));
        return courseRecordRepository.findByUserIdAndCourseIdIn(fetchCourseRecordParams.getUserIds(), fetchCourseRecordParams.getCourseIds());
    }

    public CourseRecords getCourseRecords(String userId, List<String> courseIds) {
        List<CourseRecord> crs = courseRecordRepository.findByUserIdAndCourseIdIn(List.of(userId), courseIds);
        return CourseRecords.create(userId, crs);
    }

    public List<CourseRecords> getCourseRecords(List<String> userIds, List<String> courseIds) {
        return userIds.stream().map(uid -> getCourseRecords(uid, courseIds)).toList();
    }

    public CourseRecord createCourseRecord(CourseRecord courseRecord) {
        if (courseRecordRepository.getCourseRecord(courseRecord.getUserId(), courseRecord.getCourseId()).isPresent()) {
            throw new CourseRecordAlreadyExistsException(courseRecord.getCourseId(), courseRecord.getUserId());
        }
        LocalDateTime updated = utilService.getNowDateTime();
        courseRecord.setLastUpdated(updated);
        courseRecord.getModuleRecords().forEach(mr -> moduleRecordService.createModuleRecord(mr, courseRecord, updated));
        return courseRecordRepository.save(courseRecord);
    }

}
