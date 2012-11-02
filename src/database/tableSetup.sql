DROP TABLE IF EXISTS Messages, Contacts, Assignments;

CREATE TABLE IF NOT EXISTS Messages(Id INT PRIMARY KEY AUTO_INCREMENT, Content TEXT, Receiver VARCHAR(255), MessageTimestamp TIMESTAMP()) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS Contacts(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255), PhoneNumber VARCHAR(255), Email VARCHAR(255), ClearanceLevel VARCHAR(255), Classification VARCHAR(255), Comment TEXT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS Assignments(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255), Latitude VARCHAR(255), Longitude VARCHAR(255), Receiver VARCHAR(255), Sender VARCHAR(255), Description TEXT, Timespan VARCHAR(255), Status VARCHAR(255), Cameraimage LONGBLOB, Streetname VARCHAR(255), Sitename VARCHAR(255)) ENGINE=InnoDB;
