package uk.gov.cslearning.record.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.CourseRecords;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRecordService {

    private final CourseRecordRepository courseRecordRepository;

    public CourseRecords getCourseRecords(String userId, List<String> courseIds) {
        List<CourseRecord> crs = courseRecordRepository.findByUserIdAndCourseIdIn(List.of(userId), courseIds);
        return CourseRecords.create(userId, crs);
    }

    public List<CourseRecords> getCourseRecords(List<String> userIds, List<String> courseIds) {
        return userIds.stream().map(uid -> getCourseRecords(uid, courseIds)).toList();
    }

}
