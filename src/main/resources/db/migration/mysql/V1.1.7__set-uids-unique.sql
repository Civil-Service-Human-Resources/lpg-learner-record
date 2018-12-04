ALTER TABLE event DROP INDEX unique_key_uid;
ALTER TABLE event ADD UNIQUE (uid);
ALTER TABLE learner ADD UNIQUE (uid);