-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 27 fév. 2025 à 16:45
-- Version du serveur : 8.0.31
-- Version de PHP : 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `pay_my_buddy`
--

-- --------------------------------------------------------

--
-- Structure de la table `compte`
--

DROP TABLE IF EXISTS `compte`;
CREATE TABLE IF NOT EXISTS `compte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `solde` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `compte`
--

INSERT INTO `compte` (`id`, `user_id`, `solde`) VALUES
(3, 10, '60.00'),
(4, 11, '10.00'),
(5, 12, '40.00');

-- --------------------------------------------------------

--
-- Structure de la table `connections`
--

DROP TABLE IF EXISTS `connections`;
CREATE TABLE IF NOT EXISTS `connections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `friend_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_connection` (`user_id`,`friend_id`),
  KEY `friend_id` (`friend_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `connections`
--

INSERT INTO `connections` (`id`, `user_id`, `friend_id`) VALUES
(4, 4, 5),
(3, 5, 4),
(6, 10, 11),
(8, 10, 12),
(5, 11, 10),
(7, 12, 10);

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `transactions`
--

INSERT INTO `transactions` (`id`, `sender_id`, `receiver_id`, `description`, `amount`, `created_at`) VALUES
(3, 11, 10, 'paye', '10.00', '2025-02-11 06:00:11'),
(4, 10, 11, 'paye', '10.00', '2025-02-11 08:03:31'),
(5, 12, 10, 'paye', '10.00', '2025-02-18 03:50:43'),
(6, 12, 10, 'bateau', '50.00', '2025-02-18 08:04:48');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`) VALUES
(3, 'Alice Dupont', 'alice.dupont@example.com', '$2a$10$qVzHWzKAVat/MQmAY8jrFOYQS/yceaxO1h90Z.OLnZgHoYKNmD09S'),
(4, 'Bob Martin', 'bob.martin@example.com', '$2a$10$yrVJ1CjcmkRAOrMIgzb/IOlvZp5gLOxutsAZaqdhStRz0AbXhqOt.'),
(5, 'test', 'test@test.com', '$2a$10$DqrYguH4EtGvIC4gdVOnceu.amYCQZrwO2HL.cybopppT3z9hxdkO'),
(6, 'test1', 'test1@test.com', '$2a$10$/SHTAYALDPpT9T6WSNiFD.g0b486GJkJlMVBLHm32ZFgib120BHIq'),
(7, 'test2', 'test2@test.com', '$2a$10$u7RXwXognAoxfYMU9fXOOOD30A99oJ20ToxldTzeoEjrT09GUnq/O'),
(8, 'test3', 'test3@test.com', '$2a$10$Vj3rtUQk/n92HQJXozPEyeB/KxYBUtVLbms0dwe/M4G6.KRl8LrXG'),
(9, 'test4', 'test4@test.com', '$2a$10$c8H3meG3d.kYtw9wx.pud.QukHbvurQ/eiFdgdJRIkQ2iemXjzDES'),
(10, 'test5', 'test5@test.com', '$2a$10$Pp.uXJViXdfSC0tN.ZicMOk8Q4D.15fwiH1v/W8yyaxA8Y.dulTmi'),
(11, 'test6', 'test6@test.com', '$2a$10$HsJB7qkDKsC50HhI.Vc.UebmdzjTyJ/BqjysvCXHeF6IysEKVmrBm'),
(12, 'test7', 'test7@test.com', '$2a$10$U8RiWDIxRGoM4vGgmZVdM.1n9e3e9xdSkAP9yyjqBr8PJQhgl0dv6');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `compte`
--
ALTER TABLE `compte`
  ADD CONSTRAINT `compte_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `connections`
--
ALTER TABLE `connections`
  ADD CONSTRAINT `connections_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `connections_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
