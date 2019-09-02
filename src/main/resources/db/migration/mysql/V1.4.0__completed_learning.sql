CREATE TABLE `completed_learning` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_record_course_id` varchar(50) NOT NULL,
  `course_record_user_id` varchar(50) NOT NULL,
  `completed_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_completed_learning_course_record_course_id` (`course_record_course_id`),
  KEY `FK_completed_learning_course_record_user_id` (`course_record_user_id`),
  CONSTRAINT `FK_completed_learning_course_record_id` FOREIGN KEY (`course_record_course_id`,`course_record_user_id`) REFERENCES `course_record` (`course_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;