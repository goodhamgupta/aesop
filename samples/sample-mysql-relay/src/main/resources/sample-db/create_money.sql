CREATE TABLE `money` (
  `id` bigint(20) NOT NULL,
  `amount` decimal(20,0) NOT NULL,
  `deleted` varchar(45) DEFAULT 'false',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



INSERT INTO `or_test`.`money`
(`id`,
`amount`,
`deleted`)
VALUES
(1, 15, 'false'),
(2, 3000, 'false');

