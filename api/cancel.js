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
    const { bookingId } = req.body || {};

    if (!bookingId) {
      return res.status(400).json({ success: false, error: 'Missing bookingId.' });
    }

    const sql = neon(process.env.DATABASE_URL);

    await sql`
      UPDATE bookings
      SET booking_status = 'Cancelled'
      WHERE booking_id = ${parseInt(bookingId)}
    `;

    return res.status(200).json({
      success: true,
      message: `Booking #${bookingId} has been cancelled.`
    });
  } catch (error) {
    console.error('Error cancelling booking:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

