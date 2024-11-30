-- Drop the database if it exists
DROP DATABASE IF EXISTS ProgMP;

-- Create the new database
CREATE DATABASE ProgMP;

-- Switch to the newly created database
USE ProgMP;

-- Create the 'nonApprovedUsers' table with appropriate columns
CREATE TABLE nonApprovedUsers (
    id_number INT(8) PRIMARY KEY,  -- Use INT(8) for id_number
    lastName VARCHAR(50),
    firstName VARCHAR(50),
    password VARCHAR(255),
    role VARCHAR(50)
);

-- Create the 'users' table with 'idNumber' as INT(8)
CREATE TABLE IF NOT EXISTS users (
    idNumber INT(8) NOT NULL PRIMARY KEY,  -- Use INT(8) for idNumber
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    role ENUM('Program Admin', 'Professor', 'ITS', 'Security Office') NOT NULL
);

-- Insert default program admin
INSERT INTO users (idNumber, firstname, lastname, password, role)
VALUES (12345678, 'April', 'Alvarez', 'password123', 'Program Admin');  -- Use INT(8) for idNumber

-- Create the 'rooms' table
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- Unique identifier for each room
    category VARCHAR(255) NOT NULL,     -- Room category (e.g., Classroom, Laboratory)
    room_name VARCHAR(255) NOT NULL UNIQUE,  -- Unique name for each room
    max_capacity INT NOT NULL,         -- Maximum capacity of the room
    tags TEXT,                        -- Additional tags for the room
    building VARCHAR(255) NOT NULL
);

-- Create 'unapproveBookings' table with professor_id as INT(8) to match 'users' table
CREATE TABLE approvedBookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professor_id INT(8),  -- professor_id is INT(8), matching 'idNumber' in 'users'
    booking_date DATE,    -- Correct column name for the date
    time_slot VARCHAR(255),
    room_name VARCHAR(255),
    room_category VARCHAR(255),
    building VARCHAR(255),
    FOREIGN KEY (professor_id) REFERENCES users(idNumber)  -- Foreign key to 'users' table
);

CREATE TABLE IF NOT EXISTS unapproveBookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professor_id INT,
    booking_date DATE,
    time_slot VARCHAR(255),
    room_name VARCHAR(255),
    room_category VARCHAR(255),
    building VARCHAR(255),
    FOREIGN KEY (professor_id) REFERENCES users(idNumber) -- Foreign key constraint
);

CREATE TABLE IF NOT EXISTS ITSNotification(
	id INT AUTO_INCREMENT PRIMARY KEY, -- primary key for notification
	sentBy INT, -- professor who sent the request
    FOREIGN KEY (sentBy) REFERENCES users(idNumber),
    sentDate DATE, -- date request was sent
    sentTime TIME, -- time the request was sent
    concern VARCHAR (300), -- Explain the concern
    onRoom INT, -- room name; for a more complicated query, use id
    taskedTo INT, -- who accepted the request
    FOREIGN KEY (taskedTo) REFERENCES users(idNumber) 
);

CREATE TABLE IF NOT EXISTS SecurityNotification(
	id INT AUTO_INCREMENT PRIMARY KEY, -- primary key for notification
	sentBy INT, -- professor who sent the request
    FOREIGN KEY (sentBy) REFERENCES users(idNumber),
    sentDate DATE, -- date request was sent
    sentTime TIME, -- time the request was sent
    concern VARCHAR (300), -- Explain the concern
    onRoom INT, -- room name; for a more complicated query, use id
    taskedTo INT, -- who accepted the request
    FOREIGN KEY (taskedTo) REFERENCES users(idNumber) 
);

CREATE TABLE IF NOT EXISTS regularSchedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professor_id INT NOT NULL,
    professor_name VARCHAR(255) NOT NULL,
    schedule_dates TEXT NOT NULL, -- Stores selected dates as a comma-separated string
    time_slot VARCHAR(255) NOT NULL,
    room_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (professor_id) REFERENCES users(idNumber)
);

ALTER TABLE approvedBookings
ADD CONSTRAINT fk_professor_approvedBookings
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

ALTER TABLE unapproveBookings
ADD CONSTRAINT fk_professor_unapproveBookings
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

ALTER TABLE regularSchedules
ADD CONSTRAINT fk_professor_regularSchedules
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

-- Add some example room data
INSERT INTO rooms (category, room_name, max_capacity, tags, building)
VALUES
    ('Classroom', 'MRE300', 30, 'whiteboard', 'MRE');
