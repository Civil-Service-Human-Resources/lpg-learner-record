CREATE TABLE `job_archive` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `last_run` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE course_record ADD COLUMN is_required BOOLEAN DEFAULT FALSE;