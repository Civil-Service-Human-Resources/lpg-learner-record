-- Course records
INSERT INTO learner_records (id, learner_record_type, learner_record_uid, learner_id, resource_id, created_timestamp)
values (1, 1, 'course-record-uid1', 'user1', 'course1', '2025-04-01 10:00:00'),
       (2, 1, 'course-record-uid2', 'user1', 'course2', '2025-04-03 10:00:00'),
       (3, 1, 'course-record-uid3', 'user1', 'course3', '2025-04-05 10:00:00'),
       (4, 1, 'course-record-uid4', 'user2', 'course1', '2025-03-01 10:00:00'),
       (5, 1, 'course-record-uid5', 'user2', 'course2', '2025-05-03 10:00:00'),
       (6, 1, 'course-record-uid6', 'user3', 'course1', '2025-01-01 10:00:00'),
       (7, 1, 'course-record-uid7', 'user3', 'course3', '2025-04-03 10:00:00');

-- Course record events
INSERT INTO learner_record_events (id, learner_record_id, learner_record_event_type, learner_record_event_source,
                                   event_timestamp)
values -- user 1
       -- course record 1
       (1, 1, 1, 1, '2025-04-01 10:00:00'),
       (2, 1, 2, 1, '2025-04-01 11:00:00'),
       (3, 1, 3, 1, '2025-04-01 12:00:00'),
       (4, 1, 4, 1, '2025-05-01 09:00:00'),
       -- course record 2
       (5, 2, 1, 1, '2025-04-03 10:00:00'),
       (6, 2, 4, 1, '2025-04-03 12:00:00'),
       -- course record 3
       (7, 3, 3, 1, '2025-04-05 10:00:00'),
       -- user 2
       -- course record 4
       (8, 4, 1, 1, '2025-03-01 10:00:00'),
       (9, 4, 4, 1, '2025-06-01 12:00:00'),
       -- course record 5
       (10, 5, 3, 1, '2025-05-03 10:00:00');
