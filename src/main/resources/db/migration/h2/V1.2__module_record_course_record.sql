ALTER TABLE `module_record` ADD COLUMN `course_id` varchar(50) NOT NULL;
ALTER TABLE `module_record` ADD COLUMN `user_id` varchar(50) NOT NULL;
ALTER TABLE `module_record` ADD FOREIGN KEY (`course_id`, `user_id`) REFERENCES `course_record` (`course_id`, `user_id`);