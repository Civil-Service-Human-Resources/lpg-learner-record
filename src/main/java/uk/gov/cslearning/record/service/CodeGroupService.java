package uk.gov.cslearning.record.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.gov.cslearning.record.domain.CourseGroup;
import uk.gov.cslearning.record.domain.CourseRecord;

public class CodeGroupService {
    public static Map<String, CourseGroup> mapToCourseGroup(List<CourseRecord> courses) {
        Map<String, CourseGroup> courseGroups = new HashMap<>();
        for (CourseRecord courseRecord : courses) {
            if (!courseGroups.containsKey(courseRecord.getUserId())) {
                addNewCourseGroup(courseGroups, courseRecord);
            } else {
                addCourseToExistingCourseGroup(courseGroups, courseRecord);
            }
        }

        return courseGroups;
    }

    private static void addNewCourseGroup(Map<String, CourseGroup> courseGroups, CourseRecord courseRecord) {
        List<CourseRecord> courseRecords = new ArrayList<>();
        courseRecords.add(courseRecord);
        Map<String, List<CourseRecord>> courseRecordsGroupedByCourseId = new HashMap<>();
        courseRecordsGroupedByCourseId.put(courseRecord.getCourseId(), courseRecords);
        courseGroups.put(courseRecord.getUserId(), new CourseGroup(courseRecord.getUserId(), courseRecordsGroupedByCourseId));
    }

    private static void addCourseToExistingCourseGroup(Map<String, CourseGroup> courseGroups, CourseRecord courseRecord) {
        Map<String, List<CourseRecord>> courseRecordsGroupedByCourseId = courseGroups.get(courseRecord.getUserId())
            .getCourseRecordsGroupedByCourseId();
        if (!courseRecordsGroupedByCourseId.containsKey(courseRecord.getCourseId())) {
            List<CourseRecord> courseRecords = new ArrayList<>();
            courseRecords.add(courseRecord);
            courseRecordsGroupedByCourseId.put(courseRecord.getCourseId(), courseRecords);
        } else {
            List<CourseRecord> courseRecords = courseRecordsGroupedByCourseId.get(courseRecord.getCourseId());
            courseRecords.add(courseRecord);
        }
    }
}
