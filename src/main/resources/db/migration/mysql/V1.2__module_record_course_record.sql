SET foreign_key_checks = 0;

ALTER TABLE module_record
  ADD COLUMN `course_id` varchar(50) NOT NULL,
  ADD COLUMN `user_id` varchar(50) NOT NULL,
  ADD FOREIGN KEY (`course_id`, `user_id`) REFERENCES `course_record` (`course_id`, `user_id`);

UPDATE module_record, course_record, course_record_module_records
  SET module_record.course_id = course_record.course_id, module_record.user_id = course_record.user_id
  WHERE course_record_module_records.module_records_id = module_record.id
    AND course_record_module_records.course_record_course_id = course_record.course_id
    AND course_record_module_records.course_record_user_id = course_record.user_id;

set foreign_key_checks = 1;