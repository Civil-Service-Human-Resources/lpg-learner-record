ALTER TABLE booking
    ADD learner_uid varchar(60);

ALTER TABLE invite
    ADD learner_uid varchar(60);

update booking b JOIN learner l on (b.learner_id = l.id)
set b.learner_uid = l.uid;

update invite i JOIN learner l on (i.learner_email = l.learner_email)
set i.learner_uid = l.uid;

DELETE
FROM booking
where learner_uid IS NULL;

DELETE
FROM invite
where learner_uid IS NULL;

ALTER TABLE booking
    MODIFY COLUMN learner_uid varchar(60) NOT NULL;

ALTER TABLE invite
    MODIFY COLUMN learner_uid varchar(60) NOT NULL;
