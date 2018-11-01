CREATE TABLE `event` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `catalogue_id` varchar(60) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `learner` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `uid` varchar(60) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `booking` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `learner_id` UNSIGNED int NOT NULL,
  `event_id` UNSIGNED int NOT NULL,
  `booking_time` datetime NOT NULL,
  `status` enum('REQUESTED', 'CONFIRMED'),
  `payment_details` varchar(255),
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY(`learner_id`) REFERENCES `learner`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY(`event_id`) REFERENCES  `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(`id`)
);