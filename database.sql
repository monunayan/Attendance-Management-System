CREATE DATABASE IF NOT EXISTS cams;
USE cams;

-- Drop existing tables to avoid conflicts since schema is changing drastically
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS faculty_semester;
DROP TABLE IF EXISTS teacher_semester;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS semesters;

CREATE TABLE IF NOT EXISTS semesters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    semester_name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    stream VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enrollment_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    stream VARCHAR(100) NOT NULL,
    semester_id INT,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Professor', 'HOD', 'Vice Principal', 'Principal') NOT NULL,
    department VARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS faculty_semester (
    faculty_id INT,
    semester_id INT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    PRIMARY KEY(faculty_id, semester_id)
);

CREATE TABLE IF NOT EXISTS user_biometrics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    user_type ENUM('student', 'faculty') NOT NULL,
    credential_id VARCHAR(500) NOT NULL,
    public_key TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_bio (user_id, user_type)
);

CREATE TABLE IF NOT EXISTS attendance (
    student_id INT,
    faculty_id INT,
    subject_id INT,
    date DATE,
    status ENUM('P', 'A') NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    PRIMARY KEY(student_id, subject_id, date)
);

-- Insert default semesters
INSERT IGNORE INTO semesters (id, semester_name) VALUES 
(1, 'Semester 1'), (2, 'Semester 2'), (3, 'Semester 3'), (4, 'Semester 4'),
(5, 'Semester 5'), (6, 'Semester 6'), (7, 'Semester 7'), (8, 'Semester 8');

-- Insert some default subjects for testing
INSERT IGNORE INTO subjects (id, subject_name, stream) VALUES
(1, 'Data Structures', 'B.Tech'), (2, 'DBMS', 'B.Tech'), (3, 'Java Programming', 'B.Tech'),
(4, 'Pharmacology', 'B.Pharma'), (5, 'Anatomy', 'Nursing'), (6, 'Marketing Management', 'MBA');
