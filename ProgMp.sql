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
INSERT INTO users (idNumber, firstName, lastName, password, role)
VALUES (12345678, 'April', 'Alvarez', 'password123', 'Program Admin');  -- Use INT(8) for idNumber

INSERT INTO users (idNumber, firstName, lastName, password, role)
VALUES (12121212, 'Rienzel', 'Galang', 'hi', 'ITS');  -- Use INT(8) for idNumber

INSERT INTO users (idNumber, firstName, lastName, password, role)
VALUES (13131313, 'Akisha', 'Africa', 'hi', 'Professor');  -- Use INT(8) for idNumber

-- Create the 'rooms' table
CREATE TABLE IF NOT EXISTS rooms (
    category VARCHAR(255) NOT NULL,     -- Room category (e.g., Classroom, Laboratory)
    room_name VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,  -- Unique name for each room
    max_capacity INT NOT NULL,         -- Maximum capacity of the room
    tags TEXT,                        -- Additional tags for the room
    building VARCHAR(255) NOT NULL
);

-- Create 'approvedBookings' table with professor_id as INT(8) to match 'users' table
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

-- Create 'unapproveBookings' table with professor_id as INT(8) to match 'users' table
CREATE TABLE IF NOT EXISTS unapproveBookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professor_id INT(8),  -- Use INT(8) to match 'idNumber'
    booking_date DATE,
    time_slot VARCHAR(255),
    room_name VARCHAR(255),
    room_category VARCHAR(255),
    building VARCHAR(255),
    FOREIGN KEY (professor_id) REFERENCES users(idNumber)  -- Foreign key constraint
);

-- Create 'regularSchedules' table
CREATE TABLE IF NOT EXISTS regularSchedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professor_id INT(8) NOT NULL,
    professor_name VARCHAR(255) NOT NULL,
    schedule_dates TEXT NOT NULL, -- Stores selected dates as a comma-separated string
    time_slot VARCHAR(255) NOT NULL,
    room_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (professor_id) REFERENCES users(idNumber)  -- Foreign key constraint to 'users'
);

-- Create 'security_requests' table
CREATE TABLE IF NOT EXISTS security_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,       -- Unique identifier for each request
    room VARCHAR(255) NOT NULL,              -- Room name
    issue_description TEXT NOT NULL,         -- Description of the issue
    status VARCHAR(50) DEFAULT 'Pending',    -- Status of the request (e.g., Pending, Resolved)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for when the request was created
    accepted_by INT(8) DEFAULT NULL,            -- The ITS user who accepted the request, NULL if not accepted yet
    updated_at TIMESTAMP DEFAULT NULL,          -- Timestamp when the request was last updated
    FOREIGN KEY (accepted_by) REFERENCES users(idNumber)  -- Reference to the 'users' table for ITS user ID
);

-- Create 'its_requests' table
CREATE TABLE IF NOT EXISTS its_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,  -- Unique identifier for each request
    room VARCHAR(50) NOT NULL,                  -- Room where the issue is reported
    issue_description TEXT NOT NULL,            -- Description of the issue
    status ENUM('Pending', 'Processing', 'Declined', 'Completed') NOT NULL DEFAULT 'Pending',  -- Status of the request
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp when the request was created
    accepted_by INT(8) DEFAULT NULL,            -- The ITS user who accepted the request, NULL if not accepted yet
    updated_at TIMESTAMP DEFAULT NULL,          -- Timestamp when the request was last updated
    FOREIGN KEY (accepted_by) REFERENCES users(idNumber)  -- Reference to the 'users' table for ITS user ID
);

-- Alter the 'approvedBookings' table to add the foreign key with ON DELETE CASCADE
ALTER TABLE approvedBookings
ADD CONSTRAINT fk_professor_approvedBookings
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

-- Alter the 'unapproveBookings' table to add the foreign key with ON DELETE CASCADE
ALTER TABLE unapproveBookings
ADD CONSTRAINT fk_professor_unapproveBookings
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

-- Alter the 'regularSchedules' table to add the foreign key with ON DELETE CASCADE
ALTER TABLE regularSchedules
ADD CONSTRAINT fk_professor_regularSchedules
FOREIGN KEY (professor_id) REFERENCES users(idNumber) ON DELETE CASCADE;

ALTER TABLE its_requests
DROP FOREIGN KEY its_requests_ibfk_1;  -- Drop the old foreign key, replace with actual name

ALTER TABLE its_requests
ADD CONSTRAINT fk_accepted_by
FOREIGN KEY (accepted_by) REFERENCES users(idNumber)
ON DELETE SET NULL;

-- Add some example room data
INSERT INTO rooms (category, room_name, max_capacity, tags, building)
VALUES
    ('Classroom', 'MRE300', 30, 'whiteboard', 'MRE');
