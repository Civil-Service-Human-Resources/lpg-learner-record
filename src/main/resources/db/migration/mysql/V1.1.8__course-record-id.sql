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

insert into `course_record_new` (`course_id`, `user_id`, `state`, `preference`, `profession`, `department`,
                               `last_updated`, `course_title`)
select *
from course_record;

ALTER TABLE `module_record` ADD COLUMN `course_record_id` bigint(20) NOT NULL;

UPDATE `module_record`, `course_record_module_records` SET `module_record`.`course_record_id` =
  (SELECT `id`
    FROM `course_record_new` crn, `course_record_module_records` crmr
    WHERE crn.user_id = crmr.course_record_user_id
      AND crn.course_id = crmr.course_record_course_id)
  WHERE module_record.id = course_record_module_records.module_records_id;

ALTER TABLE `module_record` ADD CONSTRAINT fk_module_record_course_record_id FOREIGN KEY (`course_record_id`) REFERENCES `course_record_new`(`id`);

RENAME TABLE course_record TO course_record_old;
RENAME TABLE course_record_new TO course_record;

SET foreign_key_checks = 1;
