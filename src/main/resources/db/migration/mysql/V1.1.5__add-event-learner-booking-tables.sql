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
  `learnerId` bigint NOT NULL,
  `eventId` bigint NOT NULL,
  `paymentDetails` varchar(255),
  `status` varchar(9) NOT NULL,
  `bookingTime` datetime NOT NULL,
  CONSTRAINT `status_type` CHECK (`status` IN ('Requested', 'Confirmed')),
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY(`learnerId`) REFERENCES `learner`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY(`eventId`) REFERENCES  `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(`learnerId`, `eventId`)
);