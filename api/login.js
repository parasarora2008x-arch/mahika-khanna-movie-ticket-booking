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
    const { username, password } = req.body || {};

    if (!username || !password) {
      return res.status(400).json({ success: false, error: 'Please enter both username and password.' });
    }

    const sql = neon(process.env.DATABASE_URL);

    const users = await sql`
      SELECT user_id, username, full_name, role, email
      FROM users
      WHERE (LOWER(username) = LOWER(${username.trim()}) OR LOWER(email) = LOWER(${username.trim()}))
        AND password = ${password.trim()}
      LIMIT 1
    `;

    if (users.length === 0) {
      return res.status(401).json({ success: false, error: 'Invalid credentials. Please check your username/password.' });
    }

    const user = users[0];
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
    console.error('Login error:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

