ALTER TABLE learner_record_event_sources
    ADD COLUMN uid VARCHAR(50) NOT NULL default UUID();

