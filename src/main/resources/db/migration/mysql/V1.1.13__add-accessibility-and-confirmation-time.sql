ALTER TABLE booking ADD `accessibility_options` varchar(255);
ALTER TABLE booking ADD `confirmation_time` datetime DEFAULT null;
ALTER TABLE booking ADD `cancellation_time` datetime DEFAULT null;