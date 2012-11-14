DROP TABLE IF EXISTS message, contact, assignment;

CREATE TABLE IF NOT EXISTS message(Id INT PRIMARY KEY AUTO_INCREMENT, Content TEXT, Receiver VARCHAR(255), Sender VARCHAR(255), MessageTimestamp VARCHAR(255), IsRead VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS contact(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS assignment(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255), Latitude VARCHAR(255), Longitude VARCHAR(255), Region TEXT, Agents VARCHAR(255), Sender VARCHAR(255), ExternalMission VARCHAR(255), Description TEXT, Timespan VARCHAR(255), Status VARCHAR(255), Cameraimage MEDIUMBLOB, Streetname VARCHAR(255), Sitename VARCHAR(255), Timestamp VARCHAR(255)) ENGINE=InnoDB;