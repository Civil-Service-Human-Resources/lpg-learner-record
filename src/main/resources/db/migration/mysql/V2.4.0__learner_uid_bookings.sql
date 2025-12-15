ALTER TABLE booking
    ADD learner_uid varchar(60);

update booking b JOIN learner l on (b.learner_id = l.id)
set b.learner_uid = l.uid;

ALTER TABLE booking
    MODIFY COLUMN learner_uid varchar(60) NOT NULL;
