ALTER TABLE event ADD `cancellation_reason` enum('UNAVAILABLE', 'VENUE') DEFAULT NULL;