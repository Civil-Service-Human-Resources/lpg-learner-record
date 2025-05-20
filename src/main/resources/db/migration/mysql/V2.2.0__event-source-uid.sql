ALTER TABLE learner_record.learner_record_event_sources
    ADD COLUMN uid VARCHAR(50);

UPDATE learner_record.learner_record_event_sources
SET uid = 'dummy';

ALTER TABLE learner_record.learner_record_event_sources
    MODIFY COLUMN uid varchar(50) NOT NULL;
