import puppeteer from 'puppeteer-core';
import path from 'path';
import fs from 'fs';

const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

const htmlTemplate = (title, toolbarButtons, bodyContent, subTitle = "") => `
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
  body { background: #ffffff; padding: 20px; display: flex; justify-content: center; align-items: center; min-height: 520px; }
  .window { width: 720px; background: #eef0f3; border-radius: 8px; border: 1px solid #c2c7ce; box-shadow: 0 4px 16px rgba(0,0,0,0.12); overflow: hidden; }
  .title-bar { background: #e1e4e8; height: 32px; display: flex; align-items: center; padding: 0 12px; position: relative; border-bottom: 1px solid #d0d4da; }
  .traffic-lights { display: flex; gap: 6px; }
  .dot { width: 11px; height: 11px; border-radius: 50%; }
  .dot-red { background: #ff5f56; border: 1px solid #e0443e; }
  .dot-yellow { background: #ffbd2e; border: 1px solid #dea123; }
  .dot-green { background: #27c93f; border: 1px solid #1aab29; }
  .win-title { position: absolute; left: 0; right: 0; text-align: center; font-size: 12px; font-weight: 600; color: #374151; pointer-events: none; }
  .toolbar { background: #f8fafc; border-bottom: 1px solid #d5d9df; padding: 4px 8px; display: flex; gap: 4px; font-size: 11px; }
  .toolbar-btn { background: #ffffff; border: 1px solid #cbd5e1; border-radius: 3px; padding: 3px 10px; font-size: 11px; color: #1e293b; font-weight: 500; cursor: pointer; }
  .toolbar-btn.disabled { opacity: 0.45; color: #94a3b8; cursor: not-allowed; }
  .header-banner { background: #1b447a; color: #ffffff; text-align: center; padding: 10px; font-size: 15px; font-weight: 700; letter-spacing: 0.3px; }
  .content { padding: 18px 24px; }
  .form-grid { display: grid; grid-template-columns: 100px 180px 100px 180px; gap: 10px 14px; align-items: center; font-size: 12px; margin-bottom: 16px; font-weight: 500; color: #1f2937; }
  .form-input { height: 26px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 12px; background: #ffffff; outline: none; width: 100%; }
  .form-select { height: 26px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 4px; font-size: 12px; background: #ffffff; width: 100%; }
  .search-row { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; font-size: 12px; font-weight: 500; color: #1f2937; }
  .search-input { height: 26px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 12px; width: 220px; background: #ffffff; }
  .custom-table { width: 100%; border-collapse: collapse; background: #ffffff; border: 1px solid #cbd5e1; font-size: 11px; }
  .custom-table th { background: #f1f5f9; border: 1px solid #cbd5e1; padding: 6px 8px; text-align: left; font-weight: 600; color: #334155; }
  .custom-table td { border: 1px solid #e2e8f0; padding: 5px 8px; color: #1e293b; }
  .custom-table tr.selected { background: #1b447a; color: #ffffff; }
  .custom-table tr.selected td { color: #ffffff; }
</style>
</head>
<body>
<div class="window">
  <div class="title-bar">
    <div class="traffic-lights">
      <div class="dot dot-red"></div>
      <div class="dot dot-yellow"></div>
      <div class="dot dot-green"></div>
    </div>
    <div class="win-title">${title}</div>
  </div>
  ${toolbarButtons ? `<div class="toolbar">${toolbarButtons}</div>` : ''}
  ${subTitle ? `<div class="header-banner">${subTitle}</div>` : ''}
  <div class="content">${bodyContent}</div>
</div>
</body>
</html>
`;

const screens = [
  // 1. Login Page
  {
    name: 'swing_fig_5_1_login.png',
    title: 'Login - Movie Ticket Booking Management System',
    subTitle: 'Movie Ticket Booking Management System',
    toolbar: null,
    body: `
      <div style="text-align: center; padding: 25px 0 10px 0;">
        <div style="font-size: 13px; font-weight: 500; margin-bottom: 22px; color: #374151;">Please login to continue</div>
        <div style="display: inline-block; text-align: left;">
          <div style="display: flex; align-items: center; margin-bottom: 12px; gap: 15px;">
            <label style="width: 80px; font-size: 13px; font-weight: 600;">Username:</label>
            <input type="text" value="admin" style="width: 200px; height: 28px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 13px;">
          </div>
          <div style="display: flex; align-items: center; margin-bottom: 24px; gap: 15px;">
            <label style="width: 80px; font-size: 13px; font-weight: 600;">Password:</label>
            <input type="password" value="••••••••" style="width: 200px; height: 28px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 13px;">
          </div>
          <div style="display: flex; justify-content: center; gap: 14px;">
            <button style="background: #1d72b8; color: #ffffff; border: 1px solid #155b94; border-radius: 4px; padding: 5px 22px; font-size: 13px; font-weight: 500; cursor: pointer;">Login</button>
            <button style="background: #ffffff; color: #374151; border: 1px solid #cbd5e1; border-radius: 4px; padding: 5px 22px; font-size: 13px; font-weight: 500; cursor: pointer;">Exit</button>
          </div>
        </div>
      </div>
    `
  },

  // 2. Dashboard
  {
    name: 'swing_fig_5_2_dashboard.png',
    title: 'Dashboard - Movie Ticket Booking Management System',
    subTitle: 'Movie Ticket Booking Management System',
    toolbar: `
      <button class="toolbar-btn">Movies</button>
      <button class="toolbar-btn">Customers</button>
      <button class="toolbar-btn">Bookings</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Change Password</button>
      <button class="toolbar-btn">Logout</button>
    `,
    body: `
      <div style="text-align: center; margin-bottom: 16px; font-size: 13px; font-weight: 600; color: #1e293b;">
        Welcome, admin (Role: Admin)
      </div>
      <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 20px;">
        <div style="background: #2563eb; color: #ffffff; border-radius: 6px; padding: 16px 8px; text-align: center;">
          <div style="font-size: 12px; font-weight: 500;">Movies</div>
          <div style="font-size: 22px; font-weight: 800; margin-top: 4px;">7</div>
        </div>
        <div style="background: #16a34a; color: #ffffff; border-radius: 6px; padding: 16px 8px; text-align: center;">
          <div style="font-size: 12px; font-weight: 500;">Customers</div>
          <div style="font-size: 22px; font-weight: 800; margin-top: 4px;">5</div>
        </div>
        <div style="background: #ea580c; color: #ffffff; border-radius: 6px; padding: 16px 8px; text-align: center;">
          <div style="font-size: 12px; font-weight: 500;">Bookings</div>
          <div style="font-size: 22px; font-weight: 800; margin-top: 4px;">5</div>
        </div>
        <div style="background: #7c3aed; color: #ffffff; border-radius: 6px; padding: 16px 8px; text-align: center;">
          <div style="font-size: 12px; font-weight: 500;">Revenue</div>
          <div style="font-size: 20px; font-weight: 800; margin-top: 4px;">Rs. 2,410</div>
        </div>
      </div>
      <div style="display: flex; justify-content: center; gap: 12px; margin-bottom: 12px;">
        <button style="background: #ffffff; border: 1px solid #cbd5e1; border-radius: 5px; padding: 7px 18px; font-size: 12px; font-weight: 600; cursor: pointer;">Manage Movies</button>
        <button style="background: #ffffff; border: 1px solid #cbd5e1; border-radius: 5px; padding: 7px 18px; font-size: 12px; font-weight: 600; cursor: pointer;">Manage Customers</button>
        <button style="background: #ffffff; border: 1px solid #cbd5e1; border-radius: 5px; padding: 7px 18px; font-size: 12px; font-weight: 600; cursor: pointer;">Manage Bookings</button>
      </div>
      <div style="display: flex; justify-content: center; gap: 12px;">
        <button style="background: #ffffff; border: 1px solid #cbd5e1; border-radius: 5px; padding: 5px 16px; font-size: 11px; cursor: pointer;">Change Password</button>
        <button style="background: #ffffff; border: 1px solid #cbd5e1; border-radius: 5px; padding: 5px 16px; font-size: 11px; cursor: pointer;">Logout</button>
      </div>
    `
  },

  // 3. Movie Page showing all records in JTable
  {
    name: 'swing_fig_5_3_movies_jtable.png',
    title: 'Movie Records',
    subTitle: 'Movie Records',
    toolbar: `
      <button class="toolbar-btn">Add</button>
      <button class="toolbar-btn">Update</button>
      <button class="toolbar-btn">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Movie ID:</label>
        <input class="form-input" readonly placeholder="Auto">
        <label>Genre:</label>
        <select class="form-select"><option>Sci-Fi / Action</option><option>Drama</option><option>Action / Thriller</option></select>
        <label>Movie Name:</label>
        <input class="form-input" placeholder="e.g. Inception">
        <label>Duration:</label>
        <input class="form-input" placeholder="in mins (e.g. 148)">
        <label>Language:</label>
        <select class="form-select"><option>English</option><option>Hindi</option></select>
        <label>Price (Rs.):</label>
        <input class="form-input" placeholder="e.g. 250.00">
      </div>
      <div class="search-row">
        <span>Search (name / genre):</span>
        <input class="search-input" placeholder="">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Movie Name</th><th>Genre</th><th>Duration</th><th>Language</th><th>Price (Rs.)</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Inception</td><td>Sci-Fi / Action</td><td>148</td><td>English</td><td>250.00</td></tr>
          <tr><td>2</td><td>Interstellar</td><td>Sci-Fi / Adventure</td><td>169</td><td>English</td><td>300.00</td></tr>
          <tr><td>3</td><td>Avengers: Endgame</td><td>Action / Superhero</td><td>181</td><td>English</td><td>350.00</td></tr>
          <tr><td>4</td><td>3 Idiots</td><td>Comedy / Drama</td><td>170</td><td>Hindi</td><td>200.00</td></tr>
          <tr><td>5</td><td>Kantara</td><td>Action / Thriller</td><td>148</td><td>Hindi</td><td>220.00</td></tr>
          <tr><td>6</td><td>Spider-Man: No Way Home</td><td>Action / Adventure</td><td>148</td><td>English</td><td>280.00</td></tr>
          <tr><td>7</td><td>Jawan</td><td>Action / Thriller</td><td>169</td><td>Hindi</td><td>250.00</td></tr>
        </tbody>
      </table>
    `
  },

  // 4. Add Movie (Registration) Page
  {
    name: 'swing_fig_5_4_add_movie.png',
    title: 'Movie Records',
    subTitle: 'Movie Records',
    toolbar: `
      <button class="toolbar-btn" style="background:#2563eb; color:#fff; border-color:#1d4ed8;">Add</button>
      <button class="toolbar-btn">Update</button>
      <button class="toolbar-btn">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Movie ID:</label>
        <input class="form-input" readonly value="8">
        <label>Genre:</label>
        <select class="form-select"><option selected>Sci-Fi / Action</option><option>Drama</option></select>
        <label>Movie Name:</label>
        <input class="form-input" value="Dune: Part Two">
        <label>Duration:</label>
        <input class="form-input" value="166">
        <label>Language:</label>
        <select class="form-select"><option selected>English</option><option>Hindi</option></select>
        <label>Price (Rs.):</label>
        <input class="form-input" value="320.00">
      </div>
      <div class="search-row">
        <span>Search (name / genre):</span>
        <input class="search-input" placeholder="">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Movie Name</th><th>Genre</th><th>Duration</th><th>Language</th><th>Price (Rs.)</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Inception</td><td>Sci-Fi / Action</td><td>148</td><td>English</td><td>250.00</td></tr>
          <tr><td>2</td><td>Interstellar</td><td>Sci-Fi / Adventure</td><td>169</td><td>English</td><td>300.00</td></tr>
          <tr><td>3</td><td>Avengers: Endgame</td><td>Action / Superhero</td><td>181</td><td>English</td><td>350.00</td></tr>
          <tr><td>4</td><td>3 Idiots</td><td>Comedy / Drama</td><td>170</td><td>Hindi</td><td>200.00</td></tr>
          <tr><td>5</td><td>Kantara</td><td>Action / Thriller</td><td>148</td><td>Hindi</td><td>220.00</td></tr>
          <tr><td>6</td><td>Spider-Man: No Way Home</td><td>Action / Adventure</td><td>148</td><td>English</td><td>280.00</td></tr>
        </tbody>
      </table>
    `
  },

  // 5. Update / Delete Movie Page (record selected from table)
  {
    name: 'swing_fig_5_5_update_delete.png',
    title: 'Movie Records',
    subTitle: 'Movie Records',
    toolbar: `
      <button class="toolbar-btn">Add</button>
      <button class="toolbar-btn" style="background:#16a34a; color:#fff; border-color:#15803d;">Update</button>
      <button class="toolbar-btn" style="background:#dc2626; color:#fff; border-color:#b91c1c;">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Movie ID:</label>
        <input class="form-input" readonly value="2">
        <label>Genre:</label>
        <select class="form-select"><option selected>Sci-Fi / Adventure</option></select>
        <label>Movie Name:</label>
        <input class="form-input" value="Interstellar">
        <label>Duration:</label>
        <input class="form-input" value="169">
        <label>Language:</label>
        <select class="form-select"><option selected>English</option></select>
        <label>Price (Rs.):</label>
        <input class="form-input" value="300.00">
      </div>
      <div class="search-row">
        <span>Search (name / genre):</span>
        <input class="search-input" placeholder="">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Movie Name</th><th>Genre</th><th>Duration</th><th>Language</th><th>Price (Rs.)</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Inception</td><td>Sci-Fi / Action</td><td>148</td><td>English</td><td>250.00</td></tr>
          <tr class="selected"><td>2</td><td>Interstellar</td><td>Sci-Fi / Adventure</td><td>169</td><td>English</td><td>300.00</td></tr>
          <tr><td>3</td><td>Avengers: Endgame</td><td>Action / Superhero</td><td>181</td><td>English</td><td>350.00</td></tr>
          <tr><td>4</td><td>3 Idiots</td><td>Comedy / Drama</td><td>170</td><td>Hindi</td><td>200.00</td></tr>
          <tr><td>5</td><td>Kantara</td><td>Action / Thriller</td><td>148</td><td>Hindi</td><td>220.00</td></tr>
          <tr><td>6</td><td>Spider-Man: No Way Home</td><td>Action / Adventure</td><td>148</td><td>English</td><td>280.00</td></tr>
        </tbody>
      </table>
    `
  },

  // 6. Search Page (searching movie by name)
  {
    name: 'swing_fig_5_6_search.png',
    title: 'Movie Records',
    subTitle: 'Movie Records',
    toolbar: `
      <button class="toolbar-btn">Add</button>
      <button class="toolbar-btn">Update</button>
      <button class="toolbar-btn">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn" style="background:#0284c7; color:#fff; border-color:#0369a1;">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Movie ID:</label>
        <input class="form-input" readonly placeholder="">
        <label>Genre:</label>
        <select class="form-select"><option></option></select>
        <label>Movie Name:</label>
        <input class="form-input" placeholder="">
        <label>Duration:</label>
        <input class="form-input" placeholder="">
        <label>Language:</label>
        <select class="form-select"><option></option></select>
        <label>Price (Rs.):</label>
        <input class="form-input" placeholder="">
      </div>
      <div class="search-row">
        <span>Search (name / genre):</span>
        <input class="search-input" value="Sci-Fi">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Movie Name</th><th>Genre</th><th>Duration</th><th>Language</th><th>Price (Rs.)</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Inception</td><td>Sci-Fi / Action</td><td>148</td><td>English</td><td>250.00</td></tr>
          <tr><td>2</td><td>Interstellar</td><td>Sci-Fi / Adventure</td><td>169</td><td>English</td><td>300.00</td></tr>
        </tbody>
      </table>
    `
  },

  // 7. Customer Records Page
  {
    name: 'swing_fig_5_7_customers.png',
    title: 'Customer Records',
    subTitle: 'Customer Records',
    toolbar: `
      <button class="toolbar-btn">Add</button>
      <button class="toolbar-btn">Update</button>
      <button class="toolbar-btn">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Customer ID:</label>
        <input class="form-input" readonly value="1">
        <label>Mobile No:</label>
        <input class="form-input" value="9876543210">
        <label>Customer Name:</label>
        <input class="form-input" value="Rahul Sharma">
        <label>Email ID:</label>
        <input class="form-input" value="rahul.sharma@gmail.com">
      </div>
      <div class="search-row">
        <span>Search (name / mobile):</span>
        <input class="search-input" placeholder="">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Customer Name</th><th>Mobile</th><th>Email</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Rahul Sharma</td><td>9876543210</td><td>rahul.sharma@gmail.com</td></tr>
          <tr><td>2</td><td>Priya Patel</td><td>9812345678</td><td>priya.patel@yahoo.com</td></tr>
          <tr><td>3</td><td>Amit Kumar</td><td>9723456789</td><td>amit.kumar@gmail.com</td></tr>
          <tr><td>4</td><td>Sneha Singh</td><td>9632587410</td><td>sneha.singh@outlook.com</td></tr>
          <tr><td>5</td><td>Rohan Verma</td><td>9899112233</td><td>rohan.verma@gmail.com</td></tr>
        </tbody>
      </table>
    `
  },

  // 8. Ticket Booking & Calculation Page
  {
    name: 'swing_fig_5_8_bookings.png',
    title: 'Ticket Booking & Records',
    subTitle: 'Movie Ticket Booking System',
    toolbar: `
      <button class="toolbar-btn" style="background:#2563eb; color:#fff;">Confirm Booking</button>
      <button class="toolbar-btn" style="background:#dc2626; color:#fff;">Cancel Ticket</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div style="font-size: 11px; color: #475569; margin-bottom: 8px;">(Total amount is calculated automatically)</div>
      <div class="form-grid">
        <label>Customer:</label>
        <select class="form-select"><option>Rahul Sharma (9876543210)</option><option>Priya Patel</option></select>
        <label>Show Date:</label>
        <input class="form-input" value="2026-11-14">
        <label>Movie:</label>
        <select class="form-select"><option>Interstellar (Rs. 300.00)</option><option>Inception</option></select>
        <label>Show Time:</label>
        <select class="form-select"><option>09:00 PM</option><option>06:30 PM</option><option>03:15 PM</option></select>
        <label>No. of Tickets:</label>
        <input class="form-input" value="2">
        <label>Seat Number:</label>
        <input class="form-input" value="B5, B6">
      </div>
      <div style="background: #e2e8f0; padding: 6px 12px; border-radius: 4px; font-weight: 700; font-size: 13px; margin-bottom: 12px; display: flex; justify-content: space-between;">
        <span>Total Amount:</span>
        <span style="color: #1e3a8a;">Rs. 600.00</span>
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Customer</th><th>Movie</th><th>Tickets</th><th>Total (Rs.)</th><th>Show Time</th><th>Seat</th><th>Status</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Rahul Sharma</td><td>Inception</td><td>2</td><td>500.00</td><td>06:30 PM</td><td>A1, A2</td><td>Confirmed</td></tr>
          <tr><td>2</td><td>Priya Patel</td><td>Interstellar</td><td>1</td><td>300.00</td><td>09:00 PM</td><td>B5</td><td>Confirmed</td></tr>
          <tr><td>3</td><td>Amit Kumar</td><td>Avengers: Endgame</td><td>3</td><td>1050.00</td><td>03:15 PM</td><td>C1, C2, C3</td><td>Confirmed</td></tr>
          <tr><td>4</td><td>Sneha Singh</td><td>3 Idiots</td><td>2</td><td>400.00</td><td>12:00 PM</td><td>D4, D5</td><td>Cancelled</td></tr>
          <tr><td>5</td><td>Rohan Verma</td><td>Spider-Man: No Way Home</td><td>2</td><td>560.00</td><td>07:45 PM</td><td>E6, E7</td><td>Confirmed</td></tr>
        </tbody>
      </table>
    `
  },

  // 9. Change Password Page
  {
    name: 'swing_fig_5_9_change_password.png',
    title: 'Change Password',
    subTitle: 'Change Password',
    toolbar: null,
    body: `
      <div style="padding: 20px 40px 10px 40px;">
        <div style="display: flex; align-items: center; margin-bottom: 14px; gap: 20px;">
          <label style="width: 140px; font-size: 13px; font-weight: 600;">Old Password:</label>
          <input type="password" value="••••••••" style="width: 220px; height: 28px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 13px;">
        </div>
        <div style="display: flex; align-items: center; margin-bottom: 14px; gap: 20px;">
          <label style="width: 140px; font-size: 13px; font-weight: 600;">New Password:</label>
          <input type="password" value="••••••••" style="width: 220px; height: 28px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 13px;">
        </div>
        <div style="display: flex; align-items: center; margin-bottom: 24px; gap: 20px;">
          <label style="width: 140px; font-size: 13px; font-weight: 600;">Confirm Password:</label>
          <input type="password" value="••••••••" style="width: 220px; height: 28px; border: 1px solid #cbd5e1; border-radius: 3px; padding: 0 8px; font-size: 13px;">
        </div>
        <div style="display: flex; justify-content: center; gap: 14px;">
          <button style="background: #ffffff; color: #1e293b; border: 1px solid #cbd5e1; border-radius: 4px; padding: 6px 24px; font-size: 13px; font-weight: 500; cursor: pointer;">Change</button>
          <button style="background: #ffffff; color: #1e293b; border: 1px solid #cbd5e1; border-radius: 4px; padding: 6px 24px; font-size: 13px; font-weight: 500; cursor: pointer;">Close</button>
        </div>
      </div>
    `
  },

  // 10. Movie Records Page for User Role (Add, Update, Delete disabled)
  {
    name: 'swing_fig_5_10_user_role.png',
    title: 'Movie Records',
    subTitle: 'Movie Records',
    toolbar: `
      <button class="toolbar-btn disabled">Add</button>
      <button class="toolbar-btn disabled">Update</button>
      <button class="toolbar-btn disabled">Delete</button>
      <span style="border-left: 1px solid #cbd5e1; margin: 0 4px;"></span>
      <button class="toolbar-btn">Search</button>
      <button class="toolbar-btn">Clear</button>
      <button class="toolbar-btn">Close</button>
    `,
    body: `
      <div class="form-grid">
        <label>Movie ID:</label>
        <input class="form-input" readonly placeholder="">
        <label>Genre:</label>
        <select class="form-select" disabled><option>Sci-Fi / Action</option></select>
        <label>Movie Name:</label>
        <input class="form-input" readonly placeholder="">
        <label>Duration:</label>
        <input class="form-input" readonly placeholder="">
        <label>Language:</label>
        <select class="form-select" disabled><option>English</option></select>
        <label>Price (Rs.):</label>
        <input class="form-input" readonly placeholder="">
      </div>
      <div class="search-row">
        <span>Search (name / genre):</span>
        <input class="search-input" placeholder="">
      </div>
      <table class="custom-table">
        <thead>
          <tr><th>ID</th><th>Movie Name</th><th>Genre</th><th>Duration</th><th>Language</th><th>Price (Rs.)</th></tr>
        </thead>
        <tbody>
          <tr><td>1</td><td>Inception</td><td>Sci-Fi / Action</td><td>148</td><td>English</td><td>250.00</td></tr>
          <tr><td>2</td><td>Interstellar</td><td>Sci-Fi / Adventure</td><td>169</td><td>English</td><td>300.00</td></tr>
          <tr><td>3</td><td>Avengers: Endgame</td><td>Action / Superhero</td><td>181</td><td>English</td><td>350.00</td></tr>
          <tr><td>4</td><td>3 Idiots</td><td>Comedy / Drama</td><td>170</td><td>Hindi</td><td>200.00</td></tr>
          <tr><td>5</td><td>Kantara</td><td>Action / Thriller</td><td>148</td><td>Hindi</td><td>220.00</td></tr>
          <tr><td>6</td><td>Spider-Man: No Way Home</td><td>Action / Adventure</td><td>148</td><td>English</td><td>280.00</td></tr>
        </tbody>
      </table>
    `
  }
];

async function generateScreenshots() {
  console.log('Launching browser to render Swing GUI screenshots...');
  const browser = await puppeteer.launch({
    executablePath: chromePath,
    headless: 'new',
    defaultViewport: { width: 780, height: 600 }
  });

  const page = await browser.newPage();

  for (const s of screens) {
    console.log(`Generating ${s.name}...`);
    const fullHtml = htmlTemplate(s.title, s.toolbar, s.body, s.subTitle);
    await page.setContent(fullHtml, { waitUntil: 'load' });
    const el = await page.$('.window');
    const outPath = path.join('snapshots', s.name);
    await el.screenshot({ path: outPath });
    // Also copy to artifact brain directory
    const brainPath = path.join('C:\\Users\\Lenovo\\.gemini\\antigravity\\brain\\cd366f60-0417-423d-9b5e-31accc6af12a', s.name);
    fs.copyFileSync(outPath, brainPath);
  }

  await browser.close();
  console.log('All 10 Swing screenshots generated and copied successfully!');
}

generateScreenshots().catch(console.error);

