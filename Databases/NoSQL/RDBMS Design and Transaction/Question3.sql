CREATE TABLE students (
    studentID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(30),
    lastName VARCHAR(30),
    dob DATE,
    enrol_date DATE
);

INSERT INTO students (firstName, lastName, dob, enrol_date) VALUES
('John', 'Doe', '2000-05-15', '2018-09-01'),
('Jane', 'Smith', '1999-07-20', '2017-09-01'),
('Michael', 'Johnson', '2001-03-10', '2019-09-01'),
('Emily', 'Davis', '2002-11-25', '2020-09-01'),
('William', 'Brown', '2000-08-30', '2018-09-01'),
('Olivia', 'Wilson', '2001-12-12', '2019-09-01');

