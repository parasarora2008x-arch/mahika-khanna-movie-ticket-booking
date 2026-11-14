import puppeteer from 'puppeteer-core';
import path from 'path';
import fs from 'fs';

const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

const useCaseHtml = `
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Times New Roman", Times, serif; }
  body { background: #ffffff; display: flex; justify-content: center; align-items: center; padding: 20px; }
  .diagram-container { width: 560px; height: 420px; position: relative; background: #ffffff; }
  .system-box {
    position: absolute;
    left: 140px;
    top: 10px;
    width: 280px;
    height: 395px;
    border: 2px solid #000000;
    border-radius: 2px;
    background: #ffffff;
    text-align: center;
    padding-top: 12px;
  }
  .system-title {
    font-size: 13px;
    font-weight: bold;
    color: #000000;
    margin-bottom: 12px;
    font-family: 'Times New Roman', serif;
  }
  .usecase-oval {
    width: 230px;
    height: 38px;
    border: 1.5px solid #000000;
    border-radius: 50%;
    margin: 8px auto;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 11px;
    color: #000000;
    background: #ffffff;
    padding: 0 10px;
    text-align: center;
    font-family: 'Times New Roman', serif;
  }
  .actor-left {
    position: absolute;
    left: 30px;
    top: 150px;
    text-align: center;
    font-family: 'Times New Roman', serif;
  }
  .actor-right {
    position: absolute;
    right: 30px;
    top: 150px;
    text-align: center;
    font-family: 'Times New Roman', serif;
  }
  .actor-title {
    font-size: 13px;
    font-weight: bold;
    margin-top: 6px;
  }
  svg {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
  }
</style>
</head>
<body>
<div class="diagram-container">
  <!-- System Boundary Box -->
  <div class="system-box">
    <div class="system-title">Movie Ticket Booking Management System</div>
    <div class="usecase-oval" id="uc1">Login</div>
    <div class="usecase-oval" id="uc2">View Dashboard</div>
    <div class="usecase-oval" id="uc3">Add / Update / Delete Movies</div>
    <div class="usecase-oval" id="uc4">Add / Update / Delete Customers</div>
    <div class="usecase-oval" id="uc5">Book Tickets / Calculate Total</div>
    <div class="usecase-oval" id="uc6">View and Search Records</div>
    <div class="usecase-oval" id="uc7">Change Password / Logout</div>
  </div>

  <!-- Left Actor: Admin -->
  <div class="actor-left">
    <svg width="45" height="70" viewBox="0 0 50 80">
      <circle cx="25" cy="15" r="10" stroke="black" stroke-width="2" fill="white"/>
      <line x1="25" y1="25" x2="25" y2="52" stroke="black" stroke-width="2"/>
      <line x1="10" y1="36" x2="40" y2="36" stroke="black" stroke-width="2"/>
      <line x1="25" y1="52" x2="12" y2="76" stroke="black" stroke-width="2"/>
      <line x1="25" y1="52" x2="38" y2="76" stroke="black" stroke-width="2"/>
    </svg>
    <div class="actor-title">Admin</div>
  </div>

  <!-- Right Actor: User -->
  <div class="actor-right">
    <svg width="45" height="70" viewBox="0 0 50 80">
      <circle cx="25" cy="15" r="10" stroke="black" stroke-width="2" fill="white"/>
      <line x1="25" y1="25" x2="25" y2="52" stroke="black" stroke-width="2"/>
      <line x1="10" y1="36" x2="40" y2="36" stroke="black" stroke-width="2"/>
      <line x1="25" y1="52" x2="12" y2="76" stroke="black" stroke-width="2"/>
      <line x1="25" y1="52" x2="38" y2="76" stroke="black" stroke-width="2"/>
    </svg>
    <div class="actor-title">User</div>
  </div>

  <!-- Association Lines -->
  <svg>
    <!-- Admin connects to all ovals -->
    <line x1="75" y1="185" x2="165" y2="58" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="105" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="152" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="198" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="245" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="292" stroke="black" stroke-width="1.2"/>
    <line x1="75" y1="185" x2="165" y2="338" stroke="black" stroke-width="1.2"/>

    <!-- User connects to allowed ovals -->
    <line x1="485" y1="185" x2="395" y2="58" stroke="black" stroke-width="1.2"/>
    <line x1="485" y1="185" x2="395" y2="105" stroke="black" stroke-width="1.2"/>
    <line x1="485" y1="185" x2="395" y2="245" stroke="black" stroke-width="1.2"/>
    <line x1="485" y1="185" x2="395" y2="292" stroke="black" stroke-width="1.2"/>
    <line x1="485" y1="185" x2="395" y2="338" stroke="black" stroke-width="1.2"/>
  </svg>
</div>
</body>
</html>
`;

async function gen() {
  const browser = await puppeteer.launch({
    executablePath: chromePath,
    headless: 'new',
    defaultViewport: { width: 640, height: 480 }
  });
  const page = await browser.newPage();
  await page.setContent(useCaseHtml, { waitUntil: 'load' });
  const el = await page.$('.diagram-container');
  const out = path.join('snapshots', 'figure_3_1_usecase.png');
  await el.screenshot({ path: out });
  const brainPath = path.join('C:\\Users\\Lenovo\\.gemini\\antigravity\\brain\\cd366f60-0417-423d-9b5e-31accc6af12a', 'figure_3_1_usecase.png');
  fs.copyFileSync(out, brainPath);
  await browser.close();
  console.log('Use Case diagram generated successfully!');
}

gen().catch(console.error);

