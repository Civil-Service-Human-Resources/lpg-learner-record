SET foreign_key_checks = 0;

CREATE TABLE `course_record_new` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `course_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `state` varchar(15) DEFAULT NULL,
  `preference` varchar(15) DEFAULT NULL,
  `profession` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `last_updated` datetime DEFAULT NULL,
  `course_title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`course_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `module_record` ADD COLUMN `course_record_id` bigint(20) NOT NULL;
ALTER TABLE `module_record` ADD CONSTRAINT fk_module_record_course_record_id FOREIGN KEY (`course_record_id`) REFERENCES `course_record_new`(`id`);

DROP TABLE course_record_module_records;
DROP TABLE course_record;

ALTER TABLE course_record_new RENAME TO course_record;

SET foreign_key_checks = 1;
