ALTER TABLE `notification` MODIFY `type` varchar(20) NOT NULL;

CREATE TABLE `required_learning_due_notification_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `identity_uid` varchar(50) NOT NULL,
  `identity_username` varchar(50) NOT NULL,
  `course_id` varchar(50) NOT NULL,
  `course_title` varchar(50) NOT NULL,
  `period`  varchar(50) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
