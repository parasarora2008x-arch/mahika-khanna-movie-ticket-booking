import { neon } from '@neondatabase/serverless';
import fs from 'fs';

// Read DATABASE_URL from .env.local
const envContent = fs.readFileSync('.env.local', 'utf-8');
const match = envContent.match(/DATABASE_URL="([^"]+)"/);
if (!match) {
  console.error("No DATABASE_URL found in .env.local");
  process.exit(1);
}

const sql = neon(match[1]);

async function main() {
  console.log("Connecting to Neon Postgres...");
  
  // Create tables
  await sql`
    CREATE TABLE IF NOT EXISTS movies (
      movie_id SERIAL PRIMARY KEY,
      movie_name VARCHAR(100) NOT NULL,
      genre VARCHAR(50) NOT NULL,
      duration INT NOT NULL,
      language VARCHAR(50) NOT NULL,
      ticket_price NUMERIC(10, 2) NOT NULL
    );
  `;
  console.log("✓ movies table created/verified");

  await sql`
    CREATE TABLE IF NOT EXISTS customers (
      customer_id SERIAL PRIMARY KEY,
      customer_name VARCHAR(100) NOT NULL,
      mobile VARCHAR(15) NOT NULL,
      email VARCHAR(100) NOT NULL
    );
  `;
  console.log("✓ customers table created/verified");

  await sql`
    CREATE TABLE IF NOT EXISTS bookings (
      booking_id SERIAL PRIMARY KEY,
      customer_id INT NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
      movie_id INT NOT NULL REFERENCES movies(movie_id) ON DELETE RESTRICT,
      number_of_tickets INT NOT NULL,
      ticket_price NUMERIC(10, 2) NOT NULL,
      total_amount NUMERIC(10, 2) NOT NULL,
      booking_date DATE NOT NULL,
      show_time VARCHAR(30) NOT NULL,
      seat_number VARCHAR(20) NOT NULL,
      booking_status VARCHAR(20) NOT NULL DEFAULT 'Confirmed'
    );
  `;
  console.log("✓ bookings table created/verified");

  // Check if movies already seeded
  const existingMovies = await sql`SELECT count(*) FROM movies`;
  if (parseInt(existingMovies[0].count) === 0) {
    console.log("Seeding movies...");
    await sql`
      INSERT INTO movies (movie_name, genre, duration, language, ticket_price) VALUES
      ('Inception', 'Sci-Fi / Action', 148, 'English', 250.00),
      ('Interstellar', 'Sci-Fi / Adventure', 169, 'English', 300.00),
      ('Avengers: Endgame', 'Action / Superhero', 181, 'English', 350.00),
      ('3 Idiots', 'Comedy / Drama', 170, 'Hindi', 200.00),
      ('Kantara', 'Action / Thriller', 148, 'Hindi', 220.00),
      ('Spider-Man: No Way Home', 'Action / Adventure', 148, 'English', 280.00),
      ('Jawan', 'Action / Thriller', 169, 'Hindi', 250.00);
    `;
    console.log("✓ movies seeded");
  }

  // Check if customers already seeded
  const existingCustomers = await sql`SELECT count(*) FROM customers`;
  if (parseInt(existingCustomers[0].count) === 0) {
    console.log("Seeding customers...");
    await sql`
      INSERT INTO customers (customer_name, mobile, email) VALUES
      ('Rahul Sharma', '9876543210', 'rahul.sharma@gmail.com'),
      ('Priya Patel', '9812345678', 'priya.patel@yahoo.com'),
      ('Amit Kumar', '9723456789', 'amit.kumar@gmail.com'),
      ('Sneha Singh', '9632587410', 'sneha.singh@outlook.com'),
      ('Rohan Verma', '9899112233', 'rohan.verma@gmail.com');
    `;
    console.log("✓ customers seeded");
  }

  // Check if bookings already seeded
  const existingBookings = await sql`SELECT count(*) FROM bookings`;
  if (parseInt(existingBookings[0].count) === 0) {
    console.log("Seeding bookings...");
    await sql`
      INSERT INTO bookings (customer_id, movie_id, number_of_tickets, ticket_price, total_amount, booking_date, show_time, seat_number, booking_status) VALUES
      (1, 1, 2, 250.00, 500.00, CURRENT_DATE, '06:30 PM', 'A1, A2', 'Confirmed'),
      (2, 2, 1, 300.00, 300.00, CURRENT_DATE, '09:00 PM', 'B5', 'Confirmed'),
      (3, 3, 3, 350.00, 1050.00, CURRENT_DATE, '03:15 PM', 'C1, C2, C3', 'Confirmed'),
      (4, 4, 2, 200.00, 400.00, CURRENT_DATE, '12:00 PM', 'D4, D5', 'Cancelled'),
      (5, 6, 2, 280.00, 560.00, CURRENT_DATE, '07:45 PM', 'E6, E7', 'Confirmed');
    `;
    console.log("✓ bookings seeded");
  }

  const movies = await sql`SELECT * FROM movies`;
  console.log(`Neon Cloud Database Ready! Total movies: ${movies.length}`);
}

main().catch(err => {
  console.error("Error initializing database:", err);
  process.exit(1);
});

