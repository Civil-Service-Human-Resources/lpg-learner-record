CREATE TABLE `invite` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `learner_email` VARCHAR(50) NOT NULL,
  PRIMARY KEY(`id`)
)