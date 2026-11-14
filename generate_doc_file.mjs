import {
  Document,
  Packer,
  Paragraph,
  TextRun,
  Table,
  TableRow,
  TableCell,
  ImageRun,
  AlignmentType,
  WidthType,
  BorderStyle,
  PageBreak,
  Footer,
  PageNumber,
  NumberFormat
} from 'docx';
import fs from 'fs';
import path from 'path';

const shieldPath = path.resolve('snapshots', 'poornima_shield.png');
const useCasePath = path.resolve('snapshots', 'figure_3_1_usecase.png');
const fig1Path = path.resolve('snapshots', 'swing_fig_5_1_login.png');
const fig2Path = path.resolve('snapshots', 'swing_fig_5_2_dashboard.png');
const fig3Path = path.resolve('snapshots', 'swing_fig_5_3_movies_jtable.png');
const fig4Path = path.resolve('snapshots', 'swing_fig_5_4_add_movie.png');
const fig5Path = path.resolve('snapshots', 'swing_fig_5_5_update_delete.png');
const fig6Path = path.resolve('snapshots', 'swing_fig_5_6_search.png');
const fig7Path = path.resolve('snapshots', 'swing_fig_5_7_customers.png');
const fig8Path = path.resolve('snapshots', 'swing_fig_5_8_bookings.png');
const fig9Path = path.resolve('snapshots', 'swing_fig_5_9_change_password.png');
const fig10Path = path.resolve('snapshots', 'swing_fig_5_10_user_role.png');

const FONT_FAMILY = 'Times New Roman';

function p(text = '', opts = {}) {
  const {
    bold = false,
    size = 20, // 20 half-points = 10pt
    align = AlignmentType.LEFT,
    italic = false,
    spaceBefore = 0,
    spaceAfter = 120, // 6pt
    lineSpacing = 240, // 1.0 line spacing
    children = null
  } = opts;

  if (children) {
    return new Paragraph({
      alignment: align,
      spacing: { before: spaceBefore, after: spaceAfter, line: lineSpacing },
      children
    });
  }

  return new Paragraph({
    alignment: align,
    spacing: { before: spaceBefore, after: spaceAfter, line: lineSpacing },
    children: [
      new TextRun({
        text,
        font: FONT_FAMILY,
        bold,
        italic,
        size
      })
    ]
  });
}

function bullet(text = '') {
  return new Paragraph({
    bullet: { level: 0 },
    spacing: { before: 0, after: 60, line: 240 },
    children: [
      new TextRun({
        text,
        font: FONT_FAMILY,
        size: 20
      })
    ]
  });
}

function chapterHeader(title) {
  return new Paragraph({
    alignment: AlignmentType.RIGHT,
    spacing: { before: 0, after: 240, line: 360 }, // 1.5 line spacing
    children: [
      new TextRun({
        text: title,
        font: FONT_FAMILY,
        bold: true,
        size: 28 // 14pt
      })
    ]
  });
}

function subHeader(title) {
  return new Paragraph({
    alignment: AlignmentType.LEFT,
    spacing: { before: 160, after: 80, line: 240 },
    children: [
      new TextRun({
        text: title,
        font: FONT_FAMILY,
        bold: true,
        size: 22 // 11pt
      })
    ]
  });
}

function createTable(headers, rows, colWidthsPct = []) {
  const tableRows = [];

  // Header row
  tableRows.push(
    new TableRow({
      tableHeader: true,
      children: headers.map((h, i) =>
        new TableCell({
          width: colWidthsPct[i] ? { size: colWidthsPct[i], type: WidthType.PERCENTAGE } : undefined,
          shading: { fill: 'D1D5DB' },
          margins: { top: 80, bottom: 80, left: 100, right: 100 },
          children: [
            new Paragraph({
              spacing: { before: 0, after: 0 },
              children: [
                new TextRun({
                  text: h,
                  font: FONT_FAMILY,
                  bold: true,
                  size: 19 // ~9.5pt
                })
              ]
            })
          ]
        })
      )
    })
  );

  // Data rows
  for (const r of rows) {
    tableRows.push(
      new TableRow({
        children: r.map((cellText, i) =>
          new TableCell({
            width: colWidthsPct[i] ? { size: colWidthsPct[i], type: WidthType.PERCENTAGE } : undefined,
            margins: { top: 70, bottom: 70, left: 100, right: 100 },
            children: [
              new Paragraph({
                spacing: { before: 0, after: 0 },
                children: [
                  new TextRun({
                    text: cellText,
                    font: FONT_FAMILY,
                    size: 19
                  })
                ]
              })
            ]
          })
        )
      })
    );
  }

  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    borders: {
      top: { style: BorderStyle.SINGLE, size: 6, color: '4B5563' },
      bottom: { style: BorderStyle.SINGLE, size: 6, color: '4B5563' },
      left: { style: BorderStyle.SINGLE, size: 6, color: '4B5563' },
      right: { style: BorderStyle.SINGLE, size: 6, color: '4B5563' },
      insideHorizontal: { style: BorderStyle.SINGLE, size: 4, color: '4B5563' },
      insideVertical: { style: BorderStyle.SINGLE, size: 4, color: '4B5563' }
    },
    rows: tableRows
  });
}

function figureBlock(imgPath, caption, imgWidth = 460, imgHeight = 220) {
  const imgData = fs.readFileSync(imgPath);
  return [
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 120, after: 60 },
      children: [
        new ImageRun({
          data: imgData,
          transformation: {
            width: imgWidth,
            height: imgHeight
          }
        })
      ]
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 40, after: 180 },
      children: [
        new TextRun({
          text: caption,
          font: FONT_FAMILY,
          bold: true,
          italics: true,
          size: 19
        })
      ]
    })
  ];
}

async function generateDocx() {
  console.log('Building Word Document (.docx)...');

  const shieldData = fs.readFileSync(shieldPath);

  const doc = new Document({
    styles: {
      default: {
        document: {
          run: {
            font: FONT_FAMILY,
            size: 20
          }
        }
      }
    },
    sections: [
      // ================= SECTION 1: COVER PAGE (NO PAGE NUMBER) =================
      {
        properties: {
          page: {
            margin: { top: 1200, bottom: 1200, left: 1300, right: 1300 },
            pageNumbers: { start: 1 }
          }
        },
        children: [
          p('A', { align: AlignmentType.CENTER, bold: true, size: 28, spaceBefore: 200, spaceAfter: 140 }),
          p('SOFTWARE REQUIREMENT SPECIFICATION', { align: AlignmentType.CENTER, bold: true, size: 27, spaceAfter: 140 }),
          p('ON', { align: AlignmentType.CENTER, bold: true, size: 24, spaceAfter: 140 }),
          p('MOVIE TICKET BOOKING MANAGEMENT SYSTEM', { align: AlignmentType.CENTER, bold: true, size: 28, spaceAfter: 140 }),
          p('UNDER', { align: AlignmentType.CENTER, bold: true, size: 22, spaceAfter: 140 }),
          p('MINI PROJECT LAB (NON-SYLLABUS PROJECT)', { align: AlignmentType.CENTER, bold: true, size: 25, spaceAfter: 400 }),

          // Logo Image
          new Paragraph({
            alignment: AlignmentType.CENTER,
            spacing: { before: 200, after: 500 },
            children: [
              new ImageRun({
                data: shieldData,
                transformation: {
                  width: 110,
                  height: 125
                }
              })
            ]
          }),

          // Submitted To & Submitted By (Table for precise alignment)
          new Table({
            width: { size: 100, type: WidthType.PERCENTAGE },
            borders: {
              top: { style: BorderStyle.NONE },
              bottom: { style: BorderStyle.NONE },
              left: { style: BorderStyle.NONE },
              right: { style: BorderStyle.NONE },
              insideHorizontal: { style: BorderStyle.NONE },
              insideVertical: { style: BorderStyle.NONE }
            },
            rows: [
              new TableRow({
                children: [
                  new TableCell({
                    width: { size: 50, type: WidthType.PERCENTAGE },
                    children: [
                      p('Submitted To:', { bold: true, size: 22, spaceAfter: 40 }),
                      p('Dr. Shikha Gautam', { bold: true, size: 22, spaceAfter: 20 }),
                      p('Assistant Professor', { bold: true, size: 21, spaceAfter: 0 })
                    ]
                  }),
                  new TableCell({
                    width: { size: 50, type: WidthType.PERCENTAGE },
                    children: [
                      p('Submitted By:', { bold: true, size: 22, align: AlignmentType.RIGHT, spaceAfter: 40 }),
                      p('Mahika Khanna (PIET25CS089)', { bold: true, size: 22, align: AlignmentType.RIGHT, spaceAfter: 20 }),
                      p('B.Tech CSE, 2nd Year, 3rd Sem', { size: 21, align: AlignmentType.RIGHT, spaceAfter: 0 })
                    ]
                  })
                ]
              })
            ]
          }),

          p('', { spaceAfter: 500 }),
          p('DEPARTMENT OF COMPUTER ENGINEERING', { align: AlignmentType.CENTER, bold: true, size: 23, spaceAfter: 40 }),
          p('POORNIMA INSTITUTE OF ENGINEERING & TECHNOLOGY, JAIPUR', { align: AlignmentType.CENTER, bold: true, size: 22, spaceAfter: 60 }),
          p('Academic Year (2026-27)', { align: AlignmentType.CENTER, size: 21, spaceAfter: 0 })
        ]
      },

      // ================= SECTION 2: CHAPTERS 1 TO 5 (PAGINATED 1 TO 11) =================
      {
        properties: {
          page: {
            margin: { top: 1300, bottom: 1200, left: 1300, right: 1300 },
            pageNumbers: { start: 1, formatType: NumberFormat.DECIMAL }
          }
        },
        footers: {
          default: new Footer({
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                children: [
                  new TextRun({
                    children: [PageNumber.CURRENT],
                    font: FONT_FAMILY,
                    size: 20
                  })
                ]
              })
            ]
          })
        },
        children: [
          // ---------------- PAGE 2 (Page 1) ----------------
          chapterHeader('Chapter 1: Introduction of Project'),
          subHeader('1.1 Objective of Project'),
          p('The main purpose of this project is to develop a simple desktop application for managing the movie ticket booking work of a cinema hall or theater. In many local theaters the details of movies, the show schedule, customer reservations and ticket collections are still written in registers or kept in Excel sheets. This takes a lot of time, mistakes can happen easily, and finding the record of a single booking is difficult.'),
          p('The Movie Ticket Booking Management System solves this problem. All the data is stored in a MySQL database and the user works on it through simple Java Swing screens. Using this project the user can:'),
          bullet('login securely with a username and password;'),
          bullet('add, update, delete and search movie records (movie name, genre, duration, language and ticket price);'),
          bullet('add, update, delete and search customer records (name, mobile and email);'),
          bullet('book movie tickets for a customer, select show date, show time and seat number, and the total amount is calculated automatically;'),
          bullet('prevent duplicate seat bookings for the same show;'),
          bullet('view all the records in a table, cancel a booking, and change his/her own password.'),

          subHeader('1.2 Types of Users'),
          p('The system has two types of users. The role of every user is stored in the database and the screens work according to this role.'),
          createTable(
            ['User Type', 'Who uses it', 'What the user can do'],
            [
              ['Admin', 'Theater management / staff in charge', 'Full access. Can add, update, delete, view and search records (movies, customers and bookings), cancel bookings, view total revenue, and change the password.'],
              ['User', 'Booking counter clerk / front desk staff', 'Can only view movies, search records, book tickets for customers, view all records and change his/her own password. The Add, Update and Delete buttons for movies are disabled for this user.']
            ],
            [20, 35, 45]
          ),

          // ---------------- PAGE 3 (Page 2) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          chapterHeader('Chapter 2: Requirement Analysis'),
          subHeader('2.1 Functional Requirement'),
          p('The following functions are provided by the system.'),
          createTable(
            ['No.', 'Function', 'Description'],
            [
              ['1', 'Login System', 'The user enters username and password. They are checked in the users table of MySQL and the user enters as Admin or User according to the role.'],
              ['2', 'Dashboard', 'Shows the total number of movies, customers, bookings and total revenue, and has buttons to open every module.'],
              ['3', 'Add Record', 'New movie, customer and booking records are entered in a Swing form and saved in the database after validation.'],
              ['4', 'Update Record', 'The user selects a record from the table, changes the details and saves it.'],
              ['5', 'Delete Record', 'The selected record is deleted from the database only after the user confirms it.'],
              ['6', 'View All Records (JTable)', 'All records are shown in a JTable. The table can be scrolled and sorted by clicking on the column heading.'],
              ['7', 'Search Record', 'Movies are searched by name or genre, customers by name or mobile, and bookings by customer name or movie.'],
              ['8', 'MySQL Database Connectivity (JDBC)', 'The Java program is connected with the MySQL database using JDBC to perform all the CRUD operations.'],
              ['9', 'Form Validation', 'Checks that all fields are filled, phone number has 10 digits, duration and ticket price are numbers, date and show time are selected, and duplicate seat booking for the same show is not entered.'],
              ['10', 'Menu Bar and Toolbar', 'The dashboard has a menu bar (File, Manage, Help) and a toolbar. The record screens have a toolbar with Add, Update, Delete, Search, Clear and Close buttons.'],
              ['11', 'Logout', 'Closes all the open screens and returns to the login screen without closing the application.'],
              ['12', 'Password Change', 'The user enters the old password and the new password twice. The old password is checked before the new one is saved.']
            ],
            [8, 32, 60]
          ),

          // ---------------- PAGE 4 (Page 3) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          subHeader('2.2 Technology Used'),
          createTable(
            ['Component', 'Technology'],
            [
              ['Programming Language', 'Java (JDK)'],
              ['GUI', 'Java Swing (JFrame, JPanel, JLabel, JTextField, JPasswordField, JComboBox, JButton, JTable, JMenuBar, JToolBar, JOptionPane)'],
              ['Database', 'MySQL'],
              ['Database Tool', 'MySQL Workbench'],
              ['Connectivity', 'JDBC with MySQL Connector/J (mysql-connector-j-8.4.0.jar)'],
              ['IDE', 'IntelliJ IDEA'],
              ['Operating System', 'Windows']
            ],
            [35, 65]
          ),

          subHeader('2.3 Graphical User Interface'),
          p('The project has the following screens. All of them are made using Java Swing.'),
          bullet('Login Page: has username and password fields with Login and Exit buttons. Wrong or empty input shows a message.'),
          bullet('Dashboard: shows the welcome message with the role of the user, four boxes with the total number of movies, customers, bookings and revenue, buttons to open the modules, and a menu bar and toolbar for quick navigation.'),
          bullet('Movie Page: form with Movie ID, Name, Genre, Duration, Language and Price, a search box and a JTable showing all movies.'),
          bullet('Customer Page: form with Customer ID, Name, Mobile and Email, a search box and a JTable showing all customers.'),
          bullet('Ticket Booking Page: form with Customer ID, Movie ID, Number of Tickets, Show Date, Show Time and Seat Number. The total amount is calculated automatically by the program. The JTable shows all booking records with customer name, movie name, seat, and status.'),
          bullet('Change Password Page: old password, new password and confirm password fields.'),
          p('When a row of the table is clicked, its data comes in the form so that it can be updated or deleted.'),

          // ---------------- PAGE 5 (Page 4) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          chapterHeader('Chapter 3: Design'),
          subHeader('3.1 Use Case Diagram'),
          ...figureBlock(useCasePath, 'Figure 3.1: Use Case Diagram of Movie Ticket Booking Management System', 460, 260),
          p('There are two actors in the system, Admin and User. Both of them have to login first. The Admin can manage the records of movies, customers and bookings (add, update and delete), view and search them, and also use the dashboard, change password and logout. The User can only open the dashboard, view and search the records, book tickets, change his/her password and logout.'),

          subHeader('3.2 Data Base Tables'),
          p('The database is named movie_ticket_db and has four tables. The tables are shown below.'),
          p('Table 3.1: users (stores the login details)', { bold: true, spaceAfter: 60 }),
          createTable(
            ['Column', 'Data Type', 'Description'],
            [
              ['user_id', 'INT, AUTO_INCREMENT', 'Primary key'],
              ['username', 'VARCHAR(30)', 'Unique, not null'],
              ['password', 'VARCHAR(30)', 'Not null'],
              ['role', 'VARCHAR(10)', 'Admin or User']
            ],
            [30, 35, 35]
          ),

          p('Table 3.2: movies', { bold: true, spaceBefore: 120, spaceAfter: 60 }),
          createTable(
            ['Column', 'Data Type', 'Description'],
            [
              ['movie_id', 'INT, AUTO_INCREMENT', 'Primary key'],
              ['movie_name', 'VARCHAR(50)', 'Not null'],
              ['genre', 'VARCHAR(30)', 'Not null'],
              ['duration', 'INT', 'Not null'],
              ['language', 'VARCHAR(20)', 'Not null'],
              ['ticket_price', 'DOUBLE', 'Not null']
            ],
            [30, 35, 35]
          ),

          // ---------------- PAGE 6 (Page 5) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          p('Table 3.3: customers', { bold: true, spaceAfter: 60 }),
          createTable(
            ['Column', 'Data Type', 'Description'],
            [
              ['customer_id', 'INT, AUTO_INCREMENT', 'Primary key'],
              ['customer_name', 'VARCHAR(50)', 'Not null'],
              ['mobile', 'VARCHAR(15)', 'Not null'],
              ['email', 'VARCHAR(50)', 'Not null']
            ],
            [30, 35, 35]
          ),

          p('Table 3.4: bookings', { bold: true, spaceBefore: 140, spaceAfter: 60 }),
          createTable(
            ['Column', 'Data Type', 'Description'],
            [
              ['booking_id', 'INT, AUTO_INCREMENT', 'Primary key'],
              ['customer_id', 'INT', 'Foreign key of customers(customer_id)'],
              ['movie_id', 'INT', 'Foreign key of movies(movie_id)'],
              ['number_of_tickets', 'INT', 'Not null'],
              ['ticket_price', 'DOUBLE', 'Not null'],
              ['total_amount', 'DOUBLE', 'Calculated by the program (tickets * price)'],
              ['booking_date', 'DATE', 'Not null'],
              ['show_time', 'VARCHAR(20)', 'Not null'],
              ['seat_number', 'VARCHAR(20)', 'Not null'],
              ['booking_status', 'VARCHAR(15)', 'Confirmed or Cancelled']
            ],
            [30, 35, 35]
          ),

          p('Relationship: One customer can have many bookings and one movie can also have many bookings. So the bookings table is connected with the customers and movies tables using the foreign keys customer_id and movie_id (ON DELETE RESTRICT, ON UPDATE CASCADE). The combination (movie_id, booking_date, show_time, seat_number) is checked by the application so that the same seat cannot be booked twice for the same movie show.', { spaceBefore: 160 }),

          // ---------------- PAGE 7 (Page 6) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          chapterHeader('Chapter 4: Conclusion'),
          p('In this project a Movie Ticket Booking Management System was developed using Java Swing, JDBC and MySQL. The project has a login system with two roles, a dashboard, and separate screens for movies, customers and bookings. All the common modules given for the minor project were completed: Add, Update, Delete, View all records in JTable, Search, form validation, menu bar and toolbar, logout and password change.'),
          p('Through this project I learned how to design GUI screens with Swing, how to create tables in MySQL, how to connect a Java program with a database using JDBC and PreparedStatement, and how to handle wrong input and exceptions so that the program does not stop suddenly.', { spaceBefore: 120 }),
          p('The project works as a small but complete application and the records are now stored safely in a database instead of registers. In future the project can be improved by:', { spaceBefore: 120 }),
          bullet('storing the passwords in encrypted form;'),
          bullet('printing the ticket receipt / confirmation slip for the customer;'),
          bullet('adding a visual seat layout map showing available and booked seats;'),
          bullet('adding an online payment gateway (UPI/card) module.'),

          // ---------------- PAGE 8 (Page 7) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          chapterHeader('Chapter 5: Snapshots of Your Project'),
          ...figureBlock(fig1Path, 'Figure 5.1: Login Page', 440, 200),
          ...figureBlock(fig2Path, 'Figure 5.2: Home / Dashboard', 440, 200),

          // ---------------- PAGE 9 (Page 8) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          ...figureBlock(fig3Path, 'Figure 5.3: Movie Page showing all records in JTable', 440, 200),
          ...figureBlock(fig4Path, 'Figure 5.4: Add Movie (Registration) Page', 440, 200),

          // ---------------- PAGE 10 (Page 9) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          ...figureBlock(fig5Path, 'Figure 5.5: Update / Delete Movie Page (record selected from table)', 440, 200),
          ...figureBlock(fig6Path, 'Figure 5.6: Search Page (searching movie by name)', 440, 200),

          // ---------------- PAGE 11 (Page 10) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          ...figureBlock(fig7Path, 'Figure 5.7: Customer Records Page', 440, 200),
          ...figureBlock(fig8Path, 'Figure 5.8: Ticket Booking Page', 440, 200),

          // ---------------- PAGE 12 (Page 11) ----------------
          new Paragraph({ children: [new PageBreak()] }),
          ...figureBlock(fig9Path, 'Figure 5.9: Change Password Page', 440, 200),
          ...figureBlock(fig10Path, 'Figure 5.10: Movie Page for the User role (Add, Update and Delete buttons disabled)', 440, 200)
        ]
      }
    ]
  });

  const buffer = await Packer.toBuffer(doc);
  fs.writeFileSync('PROJECT_REPORT_SRS.docx', buffer);

  // Also copy to artifact directory
  const brainDocx = path.join('C:\\Users\\Lenovo\\.gemini\\antigravity\\brain\\cd366f60-0417-423d-9b5e-31accc6af12a', 'PROJECT_REPORT_SRS.docx');
  fs.writeFileSync(brainDocx, buffer);

  console.log('PROJECT_REPORT_SRS.docx generated and saved successfully!');
}

generateDocx().catch(console.error);

