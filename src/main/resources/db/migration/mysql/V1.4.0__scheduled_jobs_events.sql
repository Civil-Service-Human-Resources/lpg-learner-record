CREATE TABLE `completed_learning_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_record_course_id` varchar(50) NOT NULL,
  `course_record_user_id` varchar(50) NOT NULL,
  `completed_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_completed_learning_event_course_records_course_record_id` FOREIGN KEY (`course_record_course_id`,`course_record_user_id`) REFERENCES `course_record` (`course_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `line_manager_required_learning_notification_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `line_manager_username` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `course_id` varchar(50) NOT NULL,
  `course_title` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `required_learning_due_notification_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `identity_uid` varchar(50) NOT NULL,
  `identity_username` varchar(50) NOT NULL,
  `course_id` varchar(50) NOT NULL,
  `course_title` varchar(50) NOT NULL,
  `period`  varchar(50) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `notification` MODIFY `type` varchar(20) NOT NULL;
