CREATE TABLE `event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_uid` varchar(60) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `learner` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `uid` varchar(60) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `booking` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `learner_id` int(10) unsigned NOT NULL,
  `event_id` int(10) unsigned NOT NULL,
  `booking_time` datetime NOT NULL,
  `status` enum('REQUESTED', 'CONFIRMED'),
  `payment_details` varchar(255),
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY(`learner_id`) REFERENCES `learner`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY(`event_id`) REFERENCES  `event`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
