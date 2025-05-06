package uk.gov.cslearning.record.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserRecordService {

    private final CourseRecordRepository courseRecordRepository;

    public UserRecordService(CourseRecordRepository courseRecordRepository) {
        this.courseRecordRepository = courseRecordRepository;
    }

    @Transactional
    public void deleteUserRecords(String uid) {
        courseRecordRepository.deleteAllByUid(uid);
    }

    @Transactional
    public void deleteRecordsLastUpdatedBefore(LocalDateTime localDateTime) {
        courseRecordRepository.deleteAllByLastUpdatedBefore(localDateTime);
    }
}
