package uk.gov.cslearning.record.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.gov.cslearning.record.domain.CourseGroup;
import uk.gov.cslearning.record.domain.CourseRecord;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeGroupServiceTest {
    private static final String USER_ID_1 = "test-user-id-1";
    private static final String USER_ID_2 = "test-user-id-2";
    private static final String USER_ID_3 = "test-user-id-3";
    private static final String COURSE_ID_1 = "test-course-id-1";
    private static final String COURSE_ID_2 = "test-course-id-2";
    private static final String COURSE_ID_3 = "test-course-id-3";
    private static final String COURSE_TITLE_1 = "test-course-title-1";
    private static final String COURSE_TITLE_2 = "test-course-title-2";
    private static final String COURSE_TITLE_3 = "test-course-title-3";

    private static List<CourseRecord> testCourseRecords;

    @BeforeClass
    public static void initialize() {
        testCourseRecords = new ArrayList<>();

        testCourseRecords.add(prepareCourseRecord(USER_ID_1, COURSE_ID_1, COURSE_TITLE_1));
        testCourseRecords.add(prepareCourseRecord(USER_ID_1, COURSE_ID_3, COURSE_TITLE_3));
        testCourseRecords.add(prepareCourseRecord(USER_ID_2, COURSE_ID_1, COURSE_TITLE_1));
        testCourseRecords.add(prepareCourseRecord(USER_ID_3, COURSE_ID_2, COURSE_TITLE_2));
        testCourseRecords.add(prepareCourseRecord(USER_ID_3, COURSE_ID_2, COURSE_TITLE_2));
    }

    private static CourseRecord prepareCourseRecord(String userId, String courseId, String courseTitle) {
        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        courseRecord.setCourseTitle(courseTitle);

        return courseRecord;
    }

    @Test
    public void shouldProperlyGroupCourseList() {
        Map<String, CourseGroup> groupedCourses = CodeGroupService.mapToCourseGroup(testCourseRecords);

        Assert.assertEquals(groupedCourses.size(), 3);

        CourseGroup groupCourseForUser1 = groupedCourses.get(USER_ID_1);
        Map<String, List<CourseRecord>> courseRecordsForUser1 = groupCourseForUser1.getCourseRecordsGroupedByCourseId();
        List<CourseRecord> user1CourseRecords1 = courseRecordsForUser1.get(COURSE_ID_1);
        Assert.assertEquals(user1CourseRecords1.size(), 1);
        Assert.assertEquals(user1CourseRecords1.get(0).getUserId(), USER_ID_1);
        Assert.assertEquals(user1CourseRecords1.get(0).getCourseId(), COURSE_ID_1);
        Assert.assertEquals(user1CourseRecords1.get(0).getCourseTitle(), COURSE_TITLE_1);
        List<CourseRecord> user1CourseRecords2 = courseRecordsForUser1.get(COURSE_ID_3);
        Assert.assertEquals(user1CourseRecords2.size(), 1);
        Assert.assertEquals(user1CourseRecords2.get(0).getUserId(), USER_ID_1);
        Assert.assertEquals(user1CourseRecords2.get(0).getCourseId(), COURSE_ID_3);
        Assert.assertEquals(user1CourseRecords2.get(0).getCourseTitle(), COURSE_TITLE_3);

        CourseGroup groupCourseForUser2 = groupedCourses.get(USER_ID_2);
        Map<String, List<CourseRecord>> courseRecordsForUser2 = groupCourseForUser2.getCourseRecordsGroupedByCourseId();
        List<CourseRecord> user2CourseRecords1 = courseRecordsForUser2.get(COURSE_ID_1);
        Assert.assertEquals(user2CourseRecords1.size(), 1);
        Assert.assertEquals(user2CourseRecords1.get(0).getUserId(), USER_ID_2);
        Assert.assertEquals(user2CourseRecords1.get(0).getCourseId(), COURSE_ID_1);
        Assert.assertEquals(user2CourseRecords1.get(0).getCourseTitle(), COURSE_TITLE_1);

        CourseGroup groupCourseForUser3 = groupedCourses.get(USER_ID_3);
        Map<String, List<CourseRecord>> courseRecordsForUser3 = groupCourseForUser3.getCourseRecordsGroupedByCourseId();
        List<CourseRecord> user3CourseRecords1 = courseRecordsForUser3.get(COURSE_ID_2);
        Assert.assertEquals(user3CourseRecords1.size(), 2);
        Assert.assertEquals(user3CourseRecords1.get(0).getUserId(), USER_ID_3);
        Assert.assertEquals(user3CourseRecords1.get(0).getCourseId(), COURSE_ID_2);
        Assert.assertEquals(user3CourseRecords1.get(0).getCourseTitle(), COURSE_TITLE_2);
        Assert.assertEquals(user3CourseRecords1.get(1).getUserId(), USER_ID_3);
        Assert.assertEquals(user3CourseRecords1.get(1).getCourseId(), COURSE_ID_2);
        Assert.assertEquals(user3CourseRecords1.get(1).getCourseTitle(), COURSE_TITLE_2);
    }
}
