ALTER TABLE event
    DROP COLUMN `path`;

ALTER TABLE booking
    DROP FOREIGN KEY FK_booking_learnerId_learner_id;

ALTER TABLE booking
    DROP COLUMN `learner_id`;

DROP TABLE learner;
