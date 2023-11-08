-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 23. Okt 2023 um 11:03
-- Server-Version: 10.4.28-MariaDB
-- PHP-Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `kosten`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `abrechnungen`
--

DROP TABLE IF EXISTS `abrechnungen`;
CREATE TABLE `abrechnungen` (
  `a_id` int(11) NOT NULL,
  `k_id` int(11) NOT NULL,
  `p_id` int(11) NOT NULL,
  `anzahl` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `abrechnungen`
--

INSERT INTO `abrechnungen` (`a_id`, `k_id`, `p_id`, `anzahl`) VALUES
(5, 1, 2, 2),
(6, 2, 2, 2),
(7, 3, 2, 16),
(8, 4, 2, 3),
(9, 1, 3, 5),
(10, 2, 3, 6),
(11, 3, 3, 20),
(12, 4, 3, 5),
(13, 1, 4, 10),
(14, 2, 4, 10),
(15, 3, 4, 9),
(16, 4, 4, 19),
(17, 1, 5, 50),
(18, 2, 5, 51),
(19, 3, 5, 20),
(20, 4, 5, 50),
(21, 1, 6, 31),
(22, 2, 6, 40),
(23, 3, 6, 20),
(24, 4, 6, 10);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `kostenarten`
--

DROP TABLE IF EXISTS `kostenarten`;
CREATE TABLE `kostenarten` (
  `k_id` int(11) NOT NULL,
  `kostenart` varchar(100) NOT NULL,
  `einzelverguetung` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `kostenarten`
--

INSERT INTO `kostenarten` (`k_id`, `kostenart`, `einzelverguetung`) VALUES
(1, 'Tagegeld', 46.40),
(2, 'Übernachtung', 100.00),
(3, 'Fahrtkosten', 0.35),
(4, 'Sonstiges', 10.00);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `personen`
--

DROP TABLE IF EXISTS `personen`;
CREATE TABLE `personen` (
  `p_id` int(11) NOT NULL,
  `datum` date NOT NULL,
  `name` varchar(100) NOT NULL,
  `vorname` varchar(100) NOT NULL,
  `strasse` varchar(100) NOT NULL,
  `plz` int(5) NOT NULL,
  `ort` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `personen`
--

INSERT INTO `personen` (`p_id`, `datum`, `name`, `vorname`, `strasse`, `plz`, `ort`) VALUES
(2, '2012-12-12', 'Doc', 'Ido', 'Scrapyardstreet 12', 18934, 'Tiphares'),
(3, '2009-09-09', 'Alex', 'Inklusio', 'Harmoniestraße 23', 39123, 'Unityville'),
(4, '2011-01-01', 'Taylor', 'Diversity', 'Gleichheitsstraße 88', 88110, 'Harmonytown'),
(5, '2020-11-11', 'Casey', 'Acceptance', 'Nette Straße 7', 74321, 'Nettenburg'),
(6, '2021-08-17', 'Morgan', 'Compassion', 'Liebe Straße 9', 30394, 'Respektwedel');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `abrechnungen`
--
ALTER TABLE `abrechnungen`
  ADD PRIMARY KEY (`a_id`);

--
-- Indizes für die Tabelle `kostenarten`
--
ALTER TABLE `kostenarten`
  ADD PRIMARY KEY (`k_id`);

--
-- Indizes für die Tabelle `personen`
--
ALTER TABLE `personen`
  ADD PRIMARY KEY (`p_id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `abrechnungen`
--
ALTER TABLE `abrechnungen`
  MODIFY `a_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT für Tabelle `kostenarten`
--
ALTER TABLE `kostenarten`
  MODIFY `k_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT für Tabelle `personen`
--
ALTER TABLE `personen`
  MODIFY `p_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
