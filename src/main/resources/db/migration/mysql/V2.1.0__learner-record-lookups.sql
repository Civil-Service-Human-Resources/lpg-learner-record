-- Sources
INSERT INTO learner_record_event_sources (id, source, description)
VALUES (1, 'CSL', 'Civil Service Learning');

-- Record types
INSERT INTO learner_record_types (id, record_type)
VALUES (1, 'COURSE'),
       (2, 'MODULE'),
       (3, 'EVENT');

-- Course actions/events
INSERT INTO learner_record_event_types (id, record_type, event_type, description)
VALUES (1, 1, 'MOVE_TO_LEARNING_PLAN', 'Move a course to the learning plan, from suggested learning'),
       (2, 1, 'REMOVE_FROM_LEARNING_PLAN', 'Remove a course from the homepage learning plan'),
       (3, 1, 'REMOVE_FROM_SUGGESTIONS', 'Remove a course from suggested learning'),
       (4, 1, 'COMPLETE_COURSE', 'Complete a course');
