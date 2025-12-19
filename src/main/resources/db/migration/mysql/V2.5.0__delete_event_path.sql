ALTER TABLE event
    DROP COLUMN `path`;

ALTER TABLE booking
    DROP COLUMN `learner_id`;

DROP TABLE learner;
