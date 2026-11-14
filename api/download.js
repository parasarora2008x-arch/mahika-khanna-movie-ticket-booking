import fs from 'fs';
import path from 'path';

export default function handler(req, res) {
  const type = req.query.type || 'docx';
  let fileName = 'PROJECT_REPORT_SRS.docx';
  let downloadName = 'PROJECT_REPORT_SRS_Mahika_Khanna_PIET25CS089.docx';
  let contentType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';

  if (type === 'pdf') {
    fileName = 'PROJECT_REPORT_SRS.pdf';
    downloadName = 'PROJECT_REPORT_SRS_Mahika_Khanna_PIET25CS089.pdf';
    contentType = 'application/pdf';
  }

  const filePath = path.join(process.cwd(), 'public', fileName);

  if (!fs.existsSync(filePath)) {
    return res.status(404).send('Report file not found');
  }

  const stat = fs.statSync(filePath);
  res.writeHead(200, {
    'Content-Type': contentType,
    'Content-Length': stat.size,
    'Content-Disposition': `attachment; filename="${downloadName}"`,
    'Cache-Control': 'no-cache'
  });

  const readStream = fs.createReadStream(filePath);
  readStream.pipe(res);
}

