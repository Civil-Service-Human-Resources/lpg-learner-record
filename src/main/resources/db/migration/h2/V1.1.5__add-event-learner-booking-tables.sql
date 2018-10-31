CREATE TABLE `event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `learner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uuid` varchar(60) NOT NULL,
  PRIMARY KEY(`id`)
);

CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `learner_id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `payment_details` varchar(255),
  `status` varchar(9) NOT NULL,
  `booking_time` datetime NOT NULL,
  CONSTRAINT `status_type` CHECK (`status` IN ('Requested', 'Confirmed')),
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY(`learner_id`) REFERENCES `learner`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY(`event_id`) REFERENCES  `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(`id`)
);