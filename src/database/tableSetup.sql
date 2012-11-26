# Initial konf av databas
CREATE DATABASE TDDD36;
USE TDDD36;
CREATE USER 'serverUser'@'localhost' IDENTIFIED BY 'handdukMandel';
GRANT ALL PRIVILEGES ON TDDD36 TO serverUser WITH GRANT OPTION;
# Logga ut
exit;
# Logga in som serverUser
mysql -u serverUser -p
DROP TABLE IF EXISTS message, contact, assignment;

# Ställ in alla tabeller korrekt
CREATE TABLE IF NOT EXISTS message(Id INT PRIMARY KEY AUTO_INCREMENT, Content TEXT, Receiver VARCHAR(255), Sender VARCHAR(255), MessageTimestamp VARCHAR(255), IsRead VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS contact(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS assignment(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(255), Latitude VARCHAR(255), Longitude VARCHAR(255), Region TEXT, Agents VARCHAR(255), Sender VARCHAR(255), ExternalMission VARCHAR(255), Description TEXT, Timespan VARCHAR(255), Status VARCHAR(255), Cameraimage MEDIUMBLOB, Streetname VARCHAR(255), Sitename VARCHAR(255), Timestamp VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS queue (Id INT PRIMARY KEY AUTO_INCREMENT, contact_Id INT, json LONGTEXT, INDEX con_ind (contact_id), FOREIGN KEY (contact_id) REFERENCES contact(Id) ON DELETE CASCADE) ENGINE=INNODB;
CREATE TABLE IF NOT EXISTS login(Id INT PRIMARY KEY AUTO_INCREMENT, contact_Id INT, Password VARCHAR(255), INDEX log_ind (contact_Id), FOREIGN KEY (contact_Id) REFERENCES contact(Id) ON DELETE CASCADE) ENGINE=InnoDB;