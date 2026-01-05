-- Author: R00176611 Ice Ybanez

-- write ONE SQL script that inserts at least TWO records into each table

-- insert records into Clinic table
INSERT INTO Clinic (clinicNo, cAddress, cPhoneNo)
VALUES ('C01', '875 North Drive', '0219658466'),
       ('C02', '998 Riverview', '0214567894');

-- insert records into Staff table
INSERT INTO Staff (staffNo, fName, lName, sAddress, sDOB, salary, position, clinicNo)
VALUES ('S01', 'John', 'Doe', '789 Oak St', '1990-05-10', 50000, 'Manager', 'C01'),
       ('S02', 'Jane', 'Smith', '321 Pine St', '1985-12-20', 45000, 'Veterinary', 'C02');
       
-- insert records into Owner table
INSERT INTO Owner (ownerNo, oName, oAddress, oPhoneNo)
VALUES ('O01', 'John McClain', '986 Kildara Heights', '0875684475'),
       ('O02', 'Robert Thames', '087 Pinegrew', '0876679584');

-- insert records into Horse table
INSERT INTO Horse (horseID, hDOB, hName, hColour, clinicNo, ownerNo)
VALUES ('H01', '2012-01-22', 'Elend', 'White', 'C01', 'O01'),
       ('H02', '2013-08-01', 'Vin', 'Black', 'C02', 'O02');

-- insert records into Consultation table
INSERT INTO Consultation (consultNo, date, time, horseID, staffNo)
VALUES ('CT01', '2024-11-28', '15:30:00', 'H01', 'S01'),
       ('CT02', '2024-11-29', '14:15:00', 'H02', 'S02');

-- insert records into Treatment table
INSERT INTO Treatment (treatNo, tName, description)
VALUES ('T01', 'Omeprazole', 'Treatment for gastric ulcer'),
       ('T02', 'Antimycotic Antiseptic', 'Treatment for ringworm');

-- insert records into ConsultationTreatments table
INSERT INTO ConsultationTreatments (consultNo, treatNo)
VALUES ('CT01', 'T01'),
       ('CT02', 'T02');
