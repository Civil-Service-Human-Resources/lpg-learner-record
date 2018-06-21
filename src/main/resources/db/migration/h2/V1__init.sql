
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` varchar(50),
  `identity_uid` varchar(50) NOT NULL,
  `sent` datetime NOT NULL,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `module_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` varchar(50) NOT NULL,
  `event_id` varchar(50),
  `state` varchar(15),
  `result` varchar(10),
  `score` varchar(50),
  `rated` tinyint(1),
  `completion_date` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `course_record` (
  `course_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `state` varchar(15),
  `preference` varchar(15),
  `profession` varchar(255),
  `department` varchar(255),
  `last_updated` datetime,
  PRIMARY KEY (`course_id`, `user_id`)
);

CREATE TABLE `course_record_module_records` (
  `course_record_course_id` varchar(50) NOT NULL,
  `course_record_user_id` varchar(50) NOT NULL,
  `module_records_id` bigint NOT NULL,
  PRIMARY KEY (`course_record_course_id`,`course_record_user_id`,`module_records_id`),
  CONSTRAINT `FK_course_record_module_records_course_record_id` FOREIGN KEY (`course_record_course_id`,`course_record_user_id`) REFERENCES `course_record` (`course_id`,`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_course_record_module_records_module_records` FOREIGN KEY (`module_records_id`) REFERENCES `module_record` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

