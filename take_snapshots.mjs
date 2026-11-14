import puppeteer from 'puppeteer-core';
import path from 'path';

const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

async function capture() {
  console.log('Launching Chrome...');
  const browser = await puppeteer.launch({
    executablePath: chromePath,
    headless: 'new',
    defaultViewport: { width: 1280, height: 900 }
  });

  const page = await browser.newPage();

  // 1. Login Page
  console.log('Capturing Login Page...');
  await page.goto('https://movie-ticket-booking-alpha-eight.vercel.app/login', { waitUntil: 'networkidle2' });
  await page.screenshot({ path: path.join('snapshots', 'figure_5_1_login.png'), fullPage: false });

  // 2. Registration Tab
  console.log('Capturing Registration Page...');
  await page.click('#tabRegister');
  await new Promise(r => setTimeout(r, 600));
  await page.screenshot({ path: path.join('snapshots', 'figure_5_2_register.png'), fullPage: false });

  // 3. Perform Login to reach Dashboard
  console.log('Logging in...');
  await page.click('#tabLogin');
  await new Promise(r => setTimeout(r, 400));
  await page.type('#loginUsername', 'admin');
  await page.type('#loginPassword', 'admin123');
  await page.click('#loginBtn');
  await page.waitForNavigation({ waitUntil: 'networkidle2' });

  // 3. Dashboard
  console.log('Capturing Dashboard...');
  await new Promise(r => setTimeout(r, 1200));
  await page.screenshot({ path: path.join('snapshots', 'figure_5_3_dashboard.png'), fullPage: false });

  // 4. Booking Modal
  console.log('Opening Booking Modal...');
  const bookBtns = await page.$$('.movie-card .btn');
  if (bookBtns.length > 0) {
    await bookBtns[0].click();
    await new Promise(r => setTimeout(r, 600));
    await page.screenshot({ path: path.join('snapshots', 'figure_5_4_booking_modal.png'), fullPage: false });
    // Close modal
    await page.click('.close-btn');
    await new Promise(r => setTimeout(r, 400));
  }

  // 5. Bookings Records Table
  console.log('Capturing Bookings Table...');
  // Scroll down to table
  await page.evaluate(() => {
    window.scrollTo(0, 850);
  });
  await new Promise(r => setTimeout(r, 600));
  await page.screenshot({ path: path.join('snapshots', 'figure_5_5_bookings_table.png'), fullPage: false });

  await browser.close();
  console.log('All snapshots captured successfully!');
}

capture().catch(err => {
  console.error('Error during capture:', err);
  process.exit(1);
});

