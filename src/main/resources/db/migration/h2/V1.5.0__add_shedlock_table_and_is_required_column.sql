CREATE TABLE `shedlock` (
  `name` VARCHAR(64) NOT NULL,
  `lock_until` TIMESTAMP NOT NULL,
  `locked_at` TIMESTAMP NOT NULL,
  `locked_by` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE course_record ADD COLUMN is_required BOOLEAN DEFAULT FALSE;