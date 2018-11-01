CREATE TABLE `event` (
  `id` int NOT NULL AUTO_INCREMENT,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `learner` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` varchar(60) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `booking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `learner_id` int NOT NULL,
  `event_id` int NOT NULL,
  `booking_time` datetime NOT NULL,
  `status` enum('REQUESTED', 'CONFIRMED'),
  `payment_details` varchar(255),
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY(`learner_id`) REFERENCES `learner`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY(`event_id`) REFERENCES  `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(`id`)
);