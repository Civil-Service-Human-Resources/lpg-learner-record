CREATE TABLE `event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(60) NOT NULL,
  `path` varchar(255) NOT NULL,
  `status` enum('ACTIVE','CANCELLED'),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `learner` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `uid` varchar(60) NOT NULL,
  `learner_email` varchar(60) UNIQUE NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `booking` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `learner_id` int(10) unsigned NOT NULL,
  `event_id` int(10) unsigned NOT NULL,
  `booking_time` datetime NOT NULL,
  `status` enum('REQUESTED','CONFIRMED','CANCELLED') DEFAULT NULL,
  `payment_details` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_learner_event` (`learner_id`, `event_id`),
  KEY `FK_booking_learnerId_learner_id` (`learner_id`),
  KEY `FK_booking_eventId_event_id` (`event_id`),
  CONSTRAINT `FK_booking_eventId_event_id` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_booking_learnerId_learner_id` FOREIGN KEY (`learner_id`) REFERENCES `learner` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;