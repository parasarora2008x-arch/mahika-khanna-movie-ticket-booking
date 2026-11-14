-- =====================================================================
-- COLLEGE PRACTICAL PROJECT: MOVIE TICKET BOOKING MANAGEMENT SYSTEM
-- Subject: Database Management Systems (DBMS) / Java Programming
-- Student Project Database: movie_ticket_db
-- =====================================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS movie_ticket_db;
USE movie_ticket_db;

-- Step 2: Drop existing tables (in reverse order of foreign keys)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS movies;

-- Step 3: Create 'movies' table
CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duration INT NOT NULL,
    language VARCHAR(50) NOT NULL,
    ticket_price DOUBLE NOT NULL
);

-- Step 4: Create 'customers' table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Step 5: Create 'bookings' table (with Foreign Key constraints)
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    movie_id INT NOT NULL,
    number_of_tickets INT NOT NULL,
    ticket_price DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    booking_date DATE NOT NULL,
    show_time VARCHAR(30) NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'Confirmed',
    
    -- Relationships
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_booking_movie FOREIGN KEY (movie_id) 
        REFERENCES movies(movie_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- =====================================================================
-- SAMPLE DATA (Clean, Realistic Records for Teacher Demonstration)
-- =====================================================================

-- Insert Movies
INSERT INTO movies (movie_name, genre, duration, language, ticket_price) VALUES
('Inception', 'Sci-Fi / Action', 148, 'English', 250.00),
('Interstellar', 'Sci-Fi / Adventure', 169, 'English', 300.00),
('Avengers: Endgame', 'Action / Superhero', 181, 'English', 350.00),
('3 Idiots', 'Comedy / Drama', 170, 'Hindi', 200.00),
('Kantara', 'Action / Thriller', 148, 'Hindi', 220.00),
('Spider-Man: No Way Home', 'Action / Adventure', 148, 'English', 280.00),
('Jawan', 'Action / Thriller', 169, 'Hindi', 250.00);

-- Insert Customers
INSERT INTO customers (customer_name, mobile, email) VALUES
('Rahul Sharma', '9876543210', 'rahul.sharma@gmail.com'),
('Priya Patel', '9812345678', 'priya.patel@yahoo.com'),
('Amit Kumar', '9723456789', 'amit.kumar@gmail.com'),
('Sneha Singh', '9632587410', 'sneha.singh@outlook.com'),
('Rohan Verma', '9899112233', 'rohan.verma@gmail.com');

-- Insert Bookings
INSERT INTO bookings (customer_id, movie_id, number_of_tickets, ticket_price, total_amount, booking_date, show_time, seat_number, booking_status) VALUES
(1, 1, 2, 250.00, 500.00, CURDATE(), '06:30 PM', 'A1, A2', 'Confirmed'),
(2, 2, 1, 300.00, 300.00, CURDATE(), '09:00 PM', 'B5', 'Confirmed'),
(3, 3, 3, 350.00, 1050.00, CURDATE(), '03:15 PM', 'C1, C2, C3', 'Confirmed'),
(4, 4, 2, 200.00, 400.00, CURDATE(), '12:00 PM', 'D4, D5', 'Cancelled'),
(5, 6, 2, 280.00, 560.00, CURDATE(), '07:45 PM', 'E6, E7', 'Confirmed');

-- =====================================================================
-- VERIFICATION SELECT QUERIES
-- =====================================================================
SELECT '--- MOVIES ---' AS Table_Name;
SELECT * FROM movies;

SELECT '--- CUSTOMERS ---' AS Table_Name;
SELECT * FROM customers;

SELECT '--- BOOKINGS ---' AS Table_Name;
SELECT * FROM bookings;
