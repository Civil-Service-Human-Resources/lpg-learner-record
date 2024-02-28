package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.exception.CourseRecordNotFoundException;
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

    public List<CourseRecord> fetchCourseRecords(String userId, List<String> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            log.info(String.format("Fetching all course records for user '%s'", userId));
            return courseRecordRepository.findByUserId(userId);
        } else {
            log.info(String.format("Fetching all course records for user '%s' and course IDs '%s'", userId, courseIds));
            return courseRecordRepository.findByUserIdAndCourseIdIn(userId, courseIds);
        }
    }

    public CourseRecord createCourseRecord(CourseRecord courseRecord) {
        LocalDateTime updated = utilService.getNowDateTime();
        courseRecord.setLastUpdated(updated);
        courseRecord.getModuleRecords().forEach(mr -> moduleRecordService.createModuleRecord(mr, courseRecord, updated));
        return courseRecordRepository.save(courseRecord);
    }

}
