import puppeteer from 'puppeteer-core';
import path from 'path';
import fs from 'fs';

const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

const cropHtml = `
<!DOCTYPE html>
<html>
<head>
<style>
  body { margin: 0; padding: 0; background: transparent; }
  .crop-box { width: 140px; height: 160px; overflow: hidden; position: relative; }
  img { position: absolute; left: -10px; top: -10px; height: 180px; }
</style>
</head>
<body>
<div class="crop-box">
  <img src="file://${path.resolve('public', 'logo.jpg').replace(/\\/g, '/')}" />
</div>
</body>
</html>
`;

async function crop() {
  const browser = await puppeteer.launch({
    executablePath: chromePath,
    headless: 'new',
    defaultViewport: { width: 200, height: 200 }
  });
  const page = await browser.newPage();
  await page.setContent(cropHtml, { waitUntil: 'load' });
  const el = await page.$('.crop-box');
  const out = path.join('snapshots', 'poornima_shield.png');
  await el.screenshot({ path: out, omitBackground: true });
  const brainPath = path.join('C:\\Users\\Lenovo\\.gemini\\antigravity\\brain\\cd366f60-0417-423d-9b5e-31accc6af12a', 'poornima_shield.png');
  fs.copyFileSync(out, brainPath);
  await browser.close();
  console.log('Poornima shield logo cropped successfully!');
}

crop().catch(console.error);

