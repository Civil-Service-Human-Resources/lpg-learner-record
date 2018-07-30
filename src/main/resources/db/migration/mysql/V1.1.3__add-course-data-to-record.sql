
ALTER TABLE course_record ADD COLUMN course_title varchar(255);

ALTER TABLE module_record ADD COLUMN module_title varchar(255);
ALTER TABLE module_record ADD COLUMN module_type varchar(50);
ALTER TABLE module_record ADD COLUMN duration numeric(10);
ALTER TABLE module_record ADD COLUMN event_date datetime;
ALTER TABLE module_record ADD COLUMN cost numeric(10,2);
ALTER TABLE module_record ADD COLUMN optional bit(1);
