CREATE TABLE `shedlock`
(
    `name`       VARCHAR(64)  NOT NULL,
    `lock_until` TIMESTAMP    NOT NULL,
    `locked_at`  TIMESTAMP    NOT NULL,
    `locked_by`  VARCHAR(255) NOT NULL,
    PRIMARY KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE course_record
    ADD COLUMN is_required BOOLEAN DEFAULT FALSE;

CREATE TABLE `course_notification_job_history`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(64) NOT NULL,
    `started_at`       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_at`     TIMESTAMP   NULL,
    `data_acquisition` TIMESTAMP   NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO course_notification_job_history(name, started_at, completed_at, data_acquisition)
VALUES ('COMPLETED_COURSES_JOB', NOW(), NOW(), NOW());
