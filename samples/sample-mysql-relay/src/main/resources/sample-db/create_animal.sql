CREATE TABLE `animal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `deleted` varchar(5) NOT NULL DEFAULT 'false',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


INSERT INTO `animal` (`id`, `name`, `deleted`)
VALUES
	(1, 'rat', 'false'),
	(2, 'rabbit', 'false'),
	(3, 'lion', 'false'),
	(4, 'tiger', 'false'),
	(5, 'panda', 'false'),
	(6, 'sajidKhan', 'false'),
	(7, 'chipmunk', 'false'),
	(8, 'fox', 'false'),
	(9, 'leopard', 'false'),
	(10, 'elephant', 'false');
