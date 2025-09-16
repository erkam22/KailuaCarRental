DROP DATABASE IF EXISTS KailuaCarRental;
CREATE DATABASE KailuaCarRental;
USE KailuaCarRental;

-- 1. Bilgruppe
DROP TABLE IF EXISTS Bilgruppe;
CREATE TABLE Bilgruppe (
  BilgruppeID INT AUTO_INCREMENT PRIMARY KEY,
  Navn VARCHAR(50) NOT NULL UNIQUE,
  Beskrivelse TEXT
) ENGINE=InnoDB;

-- 2. Bil
DROP TABLE IF EXISTS Bil;
CREATE TABLE Bil (
  BilID INT AUTO_INCREMENT PRIMARY KEY,
  Brand VARCHAR(50) NOT NULL,
  Model VARCHAR(50) NOT NULL,
  FuelType VARCHAR(20),
  RegNr VARCHAR(20) UNIQUE NOT NULL,
  RegAar INT,
  RegMaaned INT,
  Km INT,
  BilgruppeID INT,
  CONSTRAINT fk_bil_bilgruppe FOREIGN KEY (BilgruppeID) REFERENCES Bilgruppe(BilgruppeID)
) ENGINE=InnoDB;

-- 3. Kunde
DROP TABLE IF EXISTS Kunde;
CREATE TABLE Kunde (
  KundeID INT AUTO_INCREMENT PRIMARY KEY,
  Navn VARCHAR(100) NOT NULL,
  Adresse VARCHAR(100),
  Zip VARCHAR(10),
  ByNavn VARCHAR(50),
  Mobil VARCHAR(20),
  Telefon VARCHAR(20),
  Email VARCHAR(100),
  KoerekortNr VARCHAR(50) UNIQUE NOT NULL,
  KoerekortSiden DATE
) ENGINE=InnoDB;

-- 4. Lejekontrakt
DROP TABLE IF EXISTS Lejekontrakt;
CREATE TABLE Lejekontrakt (
  KontraktID INT AUTO_INCREMENT PRIMARY KEY,
  KundeID INT,
  BilID INT,
  FraDatoTid DATETIME,
  TilDatoTid DATETIME,
  MaxKm INT,
  StartKm INT,
  CONSTRAINT fk_lejek_kunde FOREIGN KEY (KundeID) REFERENCES Kunde(KundeID),
  CONSTRAINT fk_lejek_bil FOREIGN KEY (BilID) REFERENCES Bil(BilID)
) ENGINE=InnoDB;

-- Indsæt testdata
INSERT INTO Bilgruppe (Navn, Beskrivelse) VALUES
('Luxury', 'Eksklusive biler med >3000 ccm, automat, air condition, cruise control, lædersæder'),
('Family', 'Manuelt gear, air condition, 7+ sæder'),
('Sport', 'Manuelt gear, over 200 HK');

INSERT INTO Bil (Brand, Model, FuelType, RegNr, RegAar, RegMaaned, Km, BilgruppeID) VALUES
('BMW', '7 Series', 'Benzin', 'AB123CD', 2020, 3, 20000, 1),
('Ford', 'Galaxy', 'Diesel', 'EF456GH', 2019, 5, 40000, 2),
('Audi', 'TT', 'Benzin', 'IJ789KL', 2021, 7, 15000, 3);

INSERT INTO Kunde (Navn, Adresse, Zip, ByNavn, Mobil, Telefon, Email, KoerekortNr, KoerekortSiden) VALUES
('Mette Jensen', 'Nørregade 10', '8000', 'Aarhus', '12345678', '87654321', 'mette@mail.dk', 'DL123456', '2010-01-01'),
('Lars Hansen', 'Vestervej 22', '5000', 'Odense', '23456789', '98765432', 'lars@mail.dk', 'DL234567', '2015-05-05'),
('Anna Nielsen', 'Østergade 5', '1000', 'København', '34567890', '76543210', 'anna@mail.dk', 'DL345678', '2018-08-08');

INSERT INTO Lejekontrakt (KundeID, BilID, FraDatoTid, TilDatoTid, MaxKm, StartKm) VALUES
(1, 1, '2025-09-10 10:00:00', '2025-09-15 10:00:00', 1500, 20000),
(2, 2, '2025-09-12 09:00:00', '2025-09-18 09:00:00', 2000, 40000),
(3, 3, '2025-09-14 14:00:00', '2025-09-20 14:00:00', 1000, 15000);
-- test
