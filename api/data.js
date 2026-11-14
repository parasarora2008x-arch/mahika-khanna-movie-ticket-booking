import { neon } from '@neondatabase/serverless';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  try {
    const sql = neon(process.env.DATABASE_URL);

    const movies = await sql`
      SELECT 
        movie_id AS id, 
        movie_name AS name, 
        genre, 
        duration, 
        language, 
        ticket_price::float AS price 
      FROM movies 
      ORDER BY movie_id ASC
    `;

    const customers = await sql`
      SELECT 
        customer_id AS id, 
        customer_name AS name, 
        mobile, 
        email 
      FROM customers 
      ORDER BY customer_id ASC
    `;

    const bookings = await sql`
      SELECT 
        b.booking_id AS id,
        c.customer_name AS customer,
        m.movie_name AS movie,
        b.number_of_tickets AS tickets,
        b.ticket_price::float AS price,
        b.total_amount::float AS total,
        TO_CHAR(b.booking_date, 'YYYY-MM-DD') AS date,
        b.show_time AS time,
        b.seat_number AS seat,
        b.booking_status AS status
      FROM bookings b
      JOIN customers c ON b.customer_id = c.customer_id
      JOIN movies m ON b.movie_id = m.movie_id
      ORDER BY b.booking_id DESC
    `;

    return res.status(200).json({
      success: true,
      movies,
      customers,
      bookings
    });
  } catch (error) {
    console.error('Error fetching data:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

