INSERT INTO course_record (course_id, user_id, state, preference, profession, department, last_updated, course_title,
                           is_required)
VALUES ('testCourse1', 'user1', 'IN_PROGRESS', null, null, null, '2023-01-01 10:00:00', 'Course 1', 1),
       ('testCourse2', 'user1', 'IN_PROGRESS', null, null, null, '2023-02-02 12:00:00', 'Course 2', 0),
       ('testCourse3', 'user2', 'IN_PROGRESS', null, null, null, '2023-01-01 10:00:00', 'Course 3', 0),
       ('testCourse1', 'user2', 'IN_PROGRESS', null, null, null, '2023-02-02 12:00:00', 'Course 1', 1),
       ('course1', 'user2', 'IN_PROGRESS', null, null, null, '2023-02-02 12:00:00', 'Course 1', 1);

INSERT INTO module_record (id, uid, module_id, event_id, state, result, score, rated, completion_date, payment_method,
                           payment_details, created_at, updated_at, booking_status, module_title, module_type, duration,
                           event_date, cost, optional, course_id, user_id)
VALUES (1001, null, 'testModule1', null, 'IN_PROGRESS', null, null, 0, null, null, null, null, '2023-01-01 11:11:11',
        null, 'Module 1', 'elearning', 1200, null, 0.00, false, 'testCourse1', 'user1'),
       (1002, null, 'testModule2', null, 'IN_PROGRESS', null, null, 0, null, null, null, null, '2023-01-01 11:11:11',
        null, 'Module 2', 'link', 1200, null, 0.00, false, 'testCourse1', 'user1');

