-- Author: R00176611 Ice Ybanez

-- create database
CREATE DATABASE horseClinic;
USE horseClinic;

-- create Clinic table
CREATE TABLE Clinic (
    clinicNo varchar(3) PRIMARY KEY,
    cAddress VARCHAR(60) NOT NULL,
    cPhoneNo VARCHAR(15) NOT NULL
);

-- create Staff table
CREATE TABLE Staff (
    staffNo VARCHAR(3) PRIMARY KEY,
    fName VARCHAR(15) NOT NULL,
    lName VARCHAR(15) NOT NULL,
    sAddress VARCHAR(100) NOT NULL,
    sDOB DATE NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    position VARCHAR(30) NOT NULL,
    clinicNo VARCHAR(3),
    FOREIGN KEY (clinicNo) REFERENCES Clinic(clinicNo)
);

-- create Owner table
CREATE TABLE Owner (
    ownerNo VARCHAR(3) PRIMARY KEY,
    oName VARCHAR(30) NOT NULL,
    oAddress VARCHAR(100) NOT NULL,
    oPhoneNo VARCHAR(15) NOT NULL
);

-- create Horse table
CREATE TABLE Horse (
    horseID VARCHAR(3) PRIMARY KEY,
    hDOB DATE NOT NULL,
    hName VARCHAR(30) NOT NULL,
    hColour VARCHAR(15) NOT NULL,
    clinicNo VARCHAR(3),
    ownerNo VARCHAR(3),
    FOREIGN KEY (clinicNo) REFERENCES Clinic(clinicNo),
    FOREIGN KEY (ownerNo) REFERENCES Owner(ownerNo)
);

-- create Consultation table
CREATE TABLE Consultation (
    consultNo VARCHAR(4) PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    horseID VARCHAR(3),
    staffNo VARCHAR(3),
    FOREIGN KEY (horseID) REFERENCES Horse(horseID),
    FOREIGN KEY (staffNo) REFERENCES Staff(staffNo)
);

-- create Treatment table
CREATE TABLE Treatment (
    treatNo VARCHAR(3) PRIMARY KEY,
    tName VARCHAR(100) NOT NULL,
    description TEXT NOT NULL
);

-- create ConsultationTreatment table
CREATE TABLE ConsultationTreatments (
    consultNo VARCHAR(4),
    treatNo VARCHAR(3),
    PRIMARY KEY (consultNo, treatNo),
    FOREIGN KEY (consultNo) REFERENCES Consultation(consultNo),
    FOREIGN KEY (treatNo) REFERENCES Treatment(treatNo)
);
