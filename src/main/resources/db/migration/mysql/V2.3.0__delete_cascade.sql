ALTER TABLE learner_record_events
    DROP FOREIGN KEY fk_learner_events_record;
ALTER TABLE learner_record_events
    ADD CONSTRAINT fk_learner_events_record FOREIGN KEY (learner_record_id) REFERENCES learner_records (id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE learner_records
    DROP FOREIGN KEY fk_learner_records_parent;
ALTER TABLE learner_records
    ADD CONSTRAINT fk_learner_records_parent FOREIGN KEY (parent_record_id) REFERENCES learner_records (id) ON DELETE CASCADE ON UPDATE RESTRICT;
