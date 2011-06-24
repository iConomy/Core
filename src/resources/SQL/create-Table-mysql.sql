CREATE TABLE IF NOT EXISTS `%0$` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `balance` double(64,2) NOT NULL,
  `status` int(2) NOT NULL DEFAULT '0',
  UNIQUE KEY `username` (`username`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;