CREATE TABLE `invite` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `event_id` int UNSIGNED NOT NULL,
  `learner_email` VARCHAR(150) NOT NULL,
  CONSTRAINT `FK_invite_event_id_event_id` FOREIGN KEY(`event_id`) REFERENCES `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE KEY (`event_id`, `learner_email`),
  PRIMARY KEY(`id`)
)