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
    const { username, password, fullName, email, mobile, role } = req.body || {};

    if (!username || !password || !fullName) {
      return res.status(400).json({ success: false, error: 'Username, password, and full name are required.' });
    }

    const sql = neon(process.env.DATABASE_URL);

    // Check if username already exists
    const existing = await sql`
      SELECT user_id FROM users WHERE LOWER(username) = LOWER(${username.trim()}) LIMIT 1
    `;
    if (existing.length > 0) {
      return res.status(400).json({ success: false, error: 'Username already taken. Please choose another.' });
    }

    const assignedRole = role && ['Admin', 'Student', 'User'].includes(role) ? role : 'Student';

    const newUser = await sql`
      INSERT INTO users (username, password, full_name, role, email)
      VALUES (${username.trim()}, ${password.trim()}, ${fullName.trim()}, ${assignedRole}, ${email ? email.trim() : null})
      RETURNING user_id, username, full_name, role, email
    `;

    // Also register as a customer in customers table if mobile is provided
    if (mobile) {
      await sql`
        INSERT INTO customers (customer_name, mobile, email)
        VALUES (${fullName.trim()}, ${mobile.trim()}, ${email ? email.trim() : 'student@poornima.org'})
      `;
    }

    const user = newUser[0];
    return res.status(200).json({
      success: true,
      user: {
        id: user.user_id,
        username: user.username,
        name: user.full_name,
        role: user.role,
        email: user.email
      }
    });
  } catch (error) {
    console.error('Registration error:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

