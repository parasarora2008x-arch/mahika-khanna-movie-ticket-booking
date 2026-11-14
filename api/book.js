import { neon } from '@neondatabase/serverless';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ success: false, error: 'Method not allowed' });
  }

  try {
    const { customerId, movieId, tickets, ticketPrice, totalAmount, date, time, seat } = req.body || {};

    if (!customerId || !movieId || !tickets || !ticketPrice || !totalAmount || !date || !time || !seat) {
      return res.status(400).json({ success: false, error: 'Missing required booking fields.' });
    }

    const sql = neon(process.env.DATABASE_URL);

    // Check for duplicate seat for this movie, date, and show_time
    const seatSearch = `%${seat.trim()}%`;
    const conflict = await sql`
      SELECT booking_id FROM bookings
      WHERE movie_id = ${parseInt(movieId)}
        AND booking_date = ${date}
        AND show_time = ${time}
        AND booking_status = 'Confirmed'
        AND seat_number ILIKE ${seatSearch}
      LIMIT 1
    `;

    if (conflict.length > 0) {
      return res.status(400).json({
        success: false,
        error: `Seat [${seat}] is already booked for this show time!`
      });
    }

    // Insert new booking
    const result = await sql`
      INSERT INTO bookings (
        customer_id, movie_id, number_of_tickets, ticket_price, total_amount,
        booking_date, show_time, seat_number, booking_status
      ) VALUES (
        ${parseInt(customerId)}, ${parseInt(movieId)}, ${parseInt(tickets)},
        ${parseFloat(ticketPrice)}, ${parseFloat(totalAmount)},
        ${date}, ${time}, ${seat.trim()}, 'Confirmed'
      )
      RETURNING booking_id
    `;

    return res.status(200).json({
      success: true,
      bookingId: result[0].booking_id
    });
  } catch (error) {
    console.error('Error creating booking:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

