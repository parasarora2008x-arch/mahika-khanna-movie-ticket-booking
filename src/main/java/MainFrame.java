import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * MainFrame.java
 * Main Graphical User Interface for the Movie Ticket Booking Management System.
 * Developed with Java Swing using a clean Blue, Black, and White theme.
 */
public class MainFrame extends JFrame {

    // DAOs for database interaction
    private final MovieDAO movieDAO = new MovieDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // Theme Color Palette
    private static final Color COLOR_PRIMARY = new Color(24, 43, 73);      // Dark Navy Blue
    private static final Color COLOR_SECONDARY = new Color(37, 99, 235);   // Royal Blue Accent
    private static final Color COLOR_BG = new Color(245, 247, 250);        // Soft Light Grey / White
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);    // Deep Black/Slate
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);    // Green
    private static final Color COLOR_DANGER = new Color(239, 68, 68);      // Red
    private static final Color COLOR_BORDER = new Color(226, 232, 240);

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    // UI Navigation & CardLayout
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel statusLabel;

    // Dashboard Components
    private JLabel lblTotalMoviesVal;
    private JLabel lblTotalCustomersVal;
    private JLabel lblTotalBookingsVal;
    private JLabel lblTotalRevenueVal;
    private JTable tblRecentBookings;
    private DefaultTableModel modelRecentBookings;

    // Movie Management Components
    private JTextField txtMovieId;
    private JTextField txtMovieName;
    private JComboBox<String> cmbMovieGenre;
    private JTextField txtMovieDuration;
    private JComboBox<String> cmbMovieLanguage;
    private JTextField txtMoviePrice;
    private JTextField txtMovieSearch;
    private JTable tblMovies;
    private DefaultTableModel modelMovies;

    // Customer Management Components
    private JTextField txtCustId;
    private JTextField txtCustName;
    private JTextField txtCustMobile;
    private JTextField txtCustEmail;
    private JTextField txtCustSearch;
    private JTable tblCustomers;
    private DefaultTableModel modelCustomers;

    // Booking Management Components
    private JTextField txtBookingId;
    private JComboBox<ComboItem> cmbBookingCustomer;
    private JComboBox<ComboItem> cmbBookingMovie;
    private JSpinner spnBookingTickets;
    private JTextField txtBookingPrice;
    private JTextField txtBookingTotal;
    private JTextField txtBookingDate;
    private JComboBox<String> cmbBookingTime;
    private JTextField txtBookingSeat;
    private JTextField txtBookingSearch;
    private JTable tblBookings;
    private DefaultTableModel modelBookings;

    public MainFrame() {
        setTitle("Movie Ticket Booking Management System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        refreshAllData();
    }

    /**
     * Initializes all UI components, layouts, and styles.
     */
    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // Top Header
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_BG);

        // Add 4 modules to CardLayout
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createMoviePanel(), "MOVIES");
        mainContentPanel.add(createCustomerPanel(), "CUSTOMERS");
        mainContentPanel.add(createBookingPanel(), "BOOKINGS");

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = createStatusBar();
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    // =========================================================================
    // 1. HEADER & NAVIGATION BAR
    // =========================================================================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARY);
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Title and Subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("🎬 MOVIE TICKET BOOKING MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("College Practical Project | Core Java • Java Swing • MySQL • JDBC");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(203, 213, 225));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        panel.add(titlePanel, BorderLayout.WEST);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);

        JButton btnNavDash = createNavButton("📊 Dashboard", "DASHBOARD");
        JButton btnNavMovies = createNavButton("🎬 Movies", "MOVIES");
        JButton btnNavCust = createNavButton("👥 Customers", "CUSTOMERS");
        JButton btnNavBooking = createNavButton("🎟️ Book Ticket", "BOOKINGS");

        navPanel.add(btnNavDash);
        navPanel.add(btnNavMovies);
        navPanel.add(btnNavCust);
        navPanel.add(btnNavBooking);

        panel.add(navPanel, BorderLayout.EAST);
        return panel;
    }

    private JButton createNavButton(String text, String targetCard) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 58, 102));
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 85, 140), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 58, 102));
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, targetCard);
            if ("DASHBOARD".equals(targetCard)) {
                refreshDashboard();
            } else if ("BOOKINGS".equals(targetCard)) {
                populateBookingDropdowns();
            }
        });
        return btn;
    }

    // =========================================================================
    // 2. DASHBOARD PANEL
    // =========================================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stat Cards Panel (Top)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        lblTotalMoviesVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalCustomersVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalBookingsVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalRevenueVal = new JLabel("Rs. 0.00", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Movies", lblTotalMoviesVal, "🎬", new Color(37, 99, 235)));
        statsPanel.add(createStatCard("Total Customers", lblTotalCustomersVal, "👥", new Color(13, 148, 136)));
        statsPanel.add(createStatCard("Total Bookings", lblTotalBookingsVal, "🎟️", new Color(124, 58, 237)));
        statsPanel.add(createStatCard("Total Revenue", lblTotalRevenueVal, "💰", new Color(16, 185, 129)));

        panel.add(statsPanel, BorderLayout.NORTH);

        // Center: Recent Bookings Overview & Quick Actions
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_CARD_BG);
        centerPanel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        JLabel lblRecent = new JLabel("📋 Recent Bookings (Live from MySQL Database)");
        lblRecent.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRecent.setForeground(COLOR_TEXT_DARK);

        JButton btnRefreshDash = createStyledButton("🔄 Refresh Data", COLOR_SECONDARY);
        btnRefreshDash.addActionListener(e -> refreshAllData());
        recentHeader.add(lblRecent, BorderLayout.WEST);
        recentHeader.add(btnRefreshDash, BorderLayout.EAST);
        centerPanel.add(recentHeader, BorderLayout.NORTH);

        // Recent Bookings Table
        String[] cols = {"Booking ID", "Customer", "Movie", "Tickets", "Total Amount", "Date", "Show Time", "Seat", "Status"};
        modelRecentBookings = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRecentBookings = new JTable(modelRecentBookings);
        styleTable(tblRecentBookings);
        centerPanel.add(new JScrollPane(tblRecentBookings), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top title with icon
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblIcon, BorderLayout.EAST);

        // Center Value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // 3. MOVIE MANAGEMENT PANEL
    // =========================================================================
    private JPanel createMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left Form Panel
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(340, 0));
        formContainer.setBackground(COLOR_CARD_BG);
        formContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("🎬 Movie Information");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_PRIMARY);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        formContainer.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 8, 12));
        fieldsPanel.setOpaque(false);

        txtMovieId = new JTextField();
        txtMovieId.setEditable(false);
        txtMovieId.setBackground(new Color(241, 245, 249));
        txtMovieId.setToolTipText("Auto-generated by MySQL");

        txtMovieName = new JTextField();
        cmbMovieGenre = new JComboBox<>(new String[]{
            "Action", "Sci-Fi", "Comedy", "Drama", "Thriller", "Horror", "Romance", "Adventure", "Animation"
        });
        cmbMovieGenre.setEditable(true);

        txtMovieDuration = new JTextField();
        cmbMovieLanguage = new JComboBox<>(new String[]{
            "English", "Hindi", "Telugu", "Tamil", "Kannada", "Malayalam", "Spanish", "French"
        });
        cmbMovieLanguage.setEditable(true);

        txtMoviePrice = new JTextField();

        fieldsPanel.add(new JLabel("Movie ID:"));
        fieldsPanel.add(txtMovieId);
        fieldsPanel.add(new JLabel("Movie Name: *"));
        fieldsPanel.add(txtMovieName);
        fieldsPanel.add(new JLabel("Genre:"));
        fieldsPanel.add(cmbMovieGenre);
        fieldsPanel.add(new JLabel("Duration (mins): *"));
        fieldsPanel.add(txtMovieDuration);
        fieldsPanel.add(new JLabel("Language:"));
        fieldsPanel.add(cmbMovieLanguage);
        fieldsPanel.add(new JLabel("Ticket Price (Rs): *"));
        fieldsPanel.add(txtMoviePrice);

        formContainer.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAddMovie = createStyledButton("➕ Add Movie", COLOR_SUCCESS);
        JButton btnUpdateMovie = createStyledButton("✏️ Update Movie", COLOR_SECONDARY);
        JButton btnDeleteMovie = createStyledButton("🗑️ Delete Movie", COLOR_DANGER);
        JButton btnClearMovie = createStyledButton("🧹 Clear Form", new Color(100, 116, 139));

        btnAddMovie.addActionListener(e -> handleAddMovie());
        btnUpdateMovie.addActionListener(e -> handleUpdateMovie());
        btnDeleteMovie.addActionListener(e -> handleDeleteMovie());
        btnClearMovie.addActionListener(e -> clearMovieForm());

        btnPanel.add(btnAddMovie);
        btnPanel.add(btnUpdateMovie);
        btnPanel.add(btnDeleteMovie);
        btnPanel.add(btnClearMovie);
        formContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(formContainer, BorderLayout.WEST);

        // Right Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setBackground(COLOR_CARD_BG);
        tableContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        txtMovieSearch = new JTextField();
        txtMovieSearch.putClientProperty("JTextField.placeholderText", "Search by name, genre, language, or ID...");

        JPanel searchBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchBtnBox.setOpaque(false);
        JButton btnSearchMovie = createStyledButton("🔍 Search", COLOR_SECONDARY);
        JButton btnViewAllMovies = createStyledButton("📋 View All", COLOR_PRIMARY);

        btnSearchMovie.addActionListener(e -> handleSearchMovie());
        btnViewAllMovies.addActionListener(e -> refreshMovieTable());

        searchBtnBox.add(btnSearchMovie);
        searchBtnBox.add(btnViewAllMovies);

        searchBar.add(new JLabel("Search Movie: "), BorderLayout.WEST);
        searchBar.add(txtMovieSearch, BorderLayout.CENTER);
        searchBar.add(searchBtnBox, BorderLayout.EAST);
        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Movie ID", "Movie Name", "Genre", "Duration (mins)", "Language", "Ticket Price (Rs.)"};
        modelMovies = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMovies = new JTable(modelMovies);
        styleTable(tblMovies);

        // Click row to populate form
        tblMovies.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblMovies.getSelectedRow() != -1) {
                int row = tblMovies.getSelectedRow();
                txtMovieId.setText(tblMovies.getValueAt(row, 0).toString());
                txtMovieName.setText(tblMovies.getValueAt(row, 1).toString());
                cmbMovieGenre.setSelectedItem(tblMovies.getValueAt(row, 2).toString());
                txtMovieDuration.setText(tblMovies.getValueAt(row, 3).toString());
                cmbMovieLanguage.setSelectedItem(tblMovies.getValueAt(row, 4).toString());
                txtMoviePrice.setText(tblMovies.getValueAt(row, 5).toString());
            }
        });

        tableContainer.add(new JScrollPane(tblMovies), BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 4. CUSTOMER MANAGEMENT PANEL
    // =========================================================================
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left Form Panel
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(340, 0));
        formContainer.setBackground(COLOR_CARD_BG);
        formContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("👥 Customer Information");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_PRIMARY);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        formContainer.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 8, 14));
        fieldsPanel.setOpaque(false);

        txtCustId = new JTextField();
        txtCustId.setEditable(false);
        txtCustId.setBackground(new Color(241, 245, 249));

        txtCustName = new JTextField();
        txtCustMobile = new JTextField();
        txtCustEmail = new JTextField();

        fieldsPanel.add(new JLabel("Customer ID:"));
        fieldsPanel.add(txtCustId);
        fieldsPanel.add(new JLabel("Customer Name: *"));
        fieldsPanel.add(txtCustName);
        fieldsPanel.add(new JLabel("Mobile Number: *"));
        fieldsPanel.add(txtCustMobile);
        fieldsPanel.add(new JLabel("Email Address: *"));
        fieldsPanel.add(txtCustEmail);

        formContainer.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAddCust = createStyledButton("➕ Add Customer", COLOR_SUCCESS);
        JButton btnUpdateCust = createStyledButton("✏️ Update Customer", COLOR_SECONDARY);
        JButton btnDeleteCust = createStyledButton("🗑️ Delete Customer", COLOR_DANGER);
        JButton btnClearCust = createStyledButton("🧹 Clear Form", new Color(100, 116, 139));

        btnAddCust.addActionListener(e -> handleAddCustomer());
        btnUpdateCust.addActionListener(e -> handleUpdateCustomer());
        btnDeleteCust.addActionListener(e -> handleDeleteCustomer());
        btnClearCust.addActionListener(e -> clearCustomerForm());

        btnPanel.add(btnAddCust);
        btnPanel.add(btnUpdateCust);
        btnPanel.add(btnDeleteCust);
        btnPanel.add(btnClearCust);
        formContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(formContainer, BorderLayout.WEST);

        // Right Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setBackground(COLOR_CARD_BG);
        tableContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        txtCustSearch = new JTextField();
        txtCustSearch.putClientProperty("JTextField.placeholderText", "Search by name, mobile, email, or ID...");

        JPanel searchBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchBtnBox.setOpaque(false);
        JButton btnSearchCust = createStyledButton("🔍 Search", COLOR_SECONDARY);
        JButton btnViewAllCust = createStyledButton("📋 View All Customers", COLOR_PRIMARY);

        btnSearchCust.addActionListener(e -> handleSearchCustomer());
        btnViewAllCust.addActionListener(e -> refreshCustomerTable());

        searchBtnBox.add(btnSearchCust);
        searchBtnBox.add(btnViewAllCust);

        searchBar.add(new JLabel("Search Customer: "), BorderLayout.WEST);
        searchBar.add(txtCustSearch, BorderLayout.CENTER);
        searchBar.add(searchBtnBox, BorderLayout.EAST);
        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Customer ID", "Customer Name", "Mobile Number", "Email Address"};
        modelCustomers = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustomers = new JTable(modelCustomers);
        styleTable(tblCustomers);

        // Click row to populate form
        tblCustomers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCustomers.getSelectedRow() != -1) {
                int row = tblCustomers.getSelectedRow();
                txtCustId.setText(tblCustomers.getValueAt(row, 0).toString());
                txtCustName.setText(tblCustomers.getValueAt(row, 1).toString());
                txtCustMobile.setText(tblCustomers.getValueAt(row, 2).toString());
                txtCustEmail.setText(tblCustomers.getValueAt(row, 3).toString());
            }
        });

        tableContainer.add(new JScrollPane(tblCustomers), BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 5. TICKET BOOKING MANAGEMENT PANEL
    // =========================================================================
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left Form Panel
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(380, 0));
        formContainer.setBackground(COLOR_CARD_BG);
        formContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("🎟️ Book Movie Ticket");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_PRIMARY);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        formContainer.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(9, 2, 8, 10));
        fieldsPanel.setOpaque(false);

        txtBookingId = new JTextField();
        txtBookingId.setEditable(false);
        txtBookingId.setBackground(new Color(241, 245, 249));

        cmbBookingCustomer = new JComboBox<>();
        cmbBookingMovie = new JComboBox<>();

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 50, 1);
        spnBookingTickets = new JSpinner(spinnerModel);

        txtBookingPrice = new JTextField("0.00");
        txtBookingPrice.setEditable(false);
        txtBookingPrice.setBackground(new Color(241, 245, 249));

        txtBookingTotal = new JTextField("0.00");
        txtBookingTotal.setEditable(false);
        txtBookingTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtBookingTotal.setForeground(COLOR_SECONDARY);
        txtBookingTotal.setBackground(new Color(239, 246, 255));

        // Default to today's date YYYY-MM-DD
        txtBookingDate = new JTextField(LocalDate.now().toString());

        cmbBookingTime = new JComboBox<>(new String[]{
            "10:00 AM", "01:15 PM", "04:30 PM", "07:45 PM", "10:30 PM"
        });

        txtBookingSeat = new JTextField();
        txtBookingSeat.setToolTipText("e.g. A1, A2 or B5");

        fieldsPanel.add(new JLabel("Booking ID:"));
        fieldsPanel.add(txtBookingId);
        fieldsPanel.add(new JLabel("Select Customer: *"));
        fieldsPanel.add(cmbBookingCustomer);
        fieldsPanel.add(new JLabel("Select Movie: *"));
        fieldsPanel.add(cmbBookingMovie);
        fieldsPanel.add(new JLabel("Number of Tickets: *"));
        fieldsPanel.add(spnBookingTickets);
        fieldsPanel.add(new JLabel("Ticket Price (Rs):"));
        fieldsPanel.add(txtBookingPrice);
        fieldsPanel.add(new JLabel("Total Amount (Rs):"));
        fieldsPanel.add(txtBookingTotal);
        fieldsPanel.add(new JLabel("Show Date (YYYY-MM-DD): *"));
        fieldsPanel.add(txtBookingDate);
        fieldsPanel.add(new JLabel("Show Time: *"));
        fieldsPanel.add(cmbBookingTime);
        fieldsPanel.add(new JLabel("Seat Number(s): *"));
        fieldsPanel.add(txtBookingSeat);

        // Listeners for dynamic price and total calculation
        cmbBookingMovie.addActionListener(e -> updateBookingPriceAndTotal());
        spnBookingTickets.addChangeListener(e -> updateBookingPriceAndTotal());

        formContainer.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnBookTicket = createStyledButton("🎟️ Book Ticket Now", COLOR_SUCCESS);
        btnBookTicket.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnClearBooking = createStyledButton("🧹 Clear Form", new Color(100, 116, 139));

        btnBookTicket.addActionListener(e -> handleBookTicket());
        btnClearBooking.addActionListener(e -> clearBookingForm());

        btnPanel.add(btnBookTicket);
        btnPanel.add(btnClearBooking);
        formContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(formContainer, BorderLayout.WEST);

        // Right Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setBackground(COLOR_CARD_BG);
        tableContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Search & Action Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        txtBookingSearch = new JTextField();
        txtBookingSearch.putClientProperty("JTextField.placeholderText", "Search by Booking ID, Customer, Movie, Seat, Status...");

        JPanel searchBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchBtnBox.setOpaque(false);
        JButton btnSearchBooking = createStyledButton("🔍 Search", COLOR_SECONDARY);
        JButton btnViewAllBookings = createStyledButton("📋 View All", COLOR_PRIMARY);
        JButton btnCancelBooking = createStyledButton("❌ Cancel Booking", COLOR_DANGER);

        btnSearchBooking.addActionListener(e -> handleSearchBooking());
        btnViewAllBookings.addActionListener(e -> refreshBookingTable());
        btnCancelBooking.addActionListener(e -> handleCancelBooking());

        searchBtnBox.add(btnSearchBooking);
        searchBtnBox.add(btnViewAllBookings);
        searchBtnBox.add(btnCancelBooking);

        searchBar.add(new JLabel("Search Booking: "), BorderLayout.WEST);
        searchBar.add(txtBookingSearch, BorderLayout.CENTER);
        searchBar.add(searchBtnBox, BorderLayout.EAST);
        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Booking ID", "Customer Name", "Movie Name", "Tickets", "Price", "Total (Rs)", "Date", "Time", "Seat", "Status"};
        modelBookings = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookings = new JTable(modelBookings);
        styleTable(tblBookings);

        // Custom renderer for Status column (Confirmed = Green, Cancelled = Red/Gray)
        tblBookings.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String status = (value != null) ? value.toString() : "";
                if ("Cancelled".equalsIgnoreCase(status)) {
                    l.setForeground(new Color(220, 38, 38));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                }
                return l;
            }
        });

        // Click row to populate form / select booking id
        tblBookings.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblBookings.getSelectedRow() != -1) {
                int row = tblBookings.getSelectedRow();
                txtBookingId.setText(tblBookings.getValueAt(row, 0).toString());
            }
        });

        tableContainer.add(new JScrollPane(tblBookings), BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 6. STATUS BAR
    // =========================================================================
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(5, 15, 5, 15)
        ));

        statusLabel = new JLabel("Checking database connection...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bar.add(statusLabel, BorderLayout.WEST);

        JButton btnTestDb = new JButton("⚡ Test / Reconnect DB");
        btnTestDb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnTestDb.setFocusPainted(false);
        btnTestDb.addActionListener(e -> checkAndDisplayDbStatus());
        bar.add(btnTestDb, BorderLayout.EAST);

        return bar;
    }

    // =========================================================================
    // 7. EVENT HANDLERS & LOGIC - MOVIES
    // =========================================================================
    private void handleAddMovie() {
        String name = txtMovieName.getText().trim();
        String genre = (cmbMovieGenre.getSelectedItem() != null) ? cmbMovieGenre.getSelectedItem().toString().trim() : "";
        String durationStr = txtMovieDuration.getText().trim();
        String lang = (cmbMovieLanguage.getSelectedItem() != null) ? cmbMovieLanguage.getSelectedItem().toString().trim() : "";
        String priceStr = txtMoviePrice.getText().trim();

        if (name.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Name, Duration, Price)!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr);

            if (duration <= 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "Duration must be > 0 and Price must be >= 0!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Movie movie = new Movie(name, genre, duration, lang, price);
            boolean success = movieDAO.addMovie(movie);
            if (success) {
                JOptionPane.showMessageDialog(this, "Movie added successfully with ID: " + movie.getMovieId(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearMovieForm();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add movie to MySQL database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration and Ticket Price must be valid numbers!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateMovie() {
        String idStr = txtMovieId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a movie from the table to update!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtMovieName.getText().trim();
        String genre = (cmbMovieGenre.getSelectedItem() != null) ? cmbMovieGenre.getSelectedItem().toString().trim() : "";
        String durationStr = txtMovieDuration.getText().trim();
        String lang = (cmbMovieLanguage.getSelectedItem() != null) ? cmbMovieLanguage.getSelectedItem().toString().trim() : "";
        String priceStr = txtMoviePrice.getText().trim();

        if (name.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr);

            Movie movie = new Movie(id, name, genre, duration, lang, price);
            boolean success = movieDAO.updateMovie(movie);
            if (success) {
                JOptionPane.showMessageDialog(this, "Movie updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearMovieForm();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update movie in MySQL.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID, Duration, and Price!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMovie() {
        String idStr = txtMovieId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a movie to delete!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Movie ID #" + idStr + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = movieDAO.deleteMovie(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Movie deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearMovieForm();
                    refreshAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Movie not found or could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Check if foreign key constraint failed (e.g. error code 1451)
                JOptionPane.showMessageDialog(this,
                        "Cannot delete this movie because existing bookings are linked to it.\n" +
                        "Cancel the associated bookings first or keep the movie record.",
                        "Constraint Violation", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Movie ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearchMovie() {
        String query = txtMovieSearch.getText().trim();
        if (query.isEmpty()) {
            refreshMovieTable();
            return;
        }
        List<Movie> list = movieDAO.searchMovies(query);
        populateMovieTable(list);
    }

    private void clearMovieForm() {
        txtMovieId.setText("");
        txtMovieName.setText("");
        cmbMovieGenre.setSelectedIndex(0);
        txtMovieDuration.setText("");
        cmbMovieLanguage.setSelectedIndex(0);
        txtMoviePrice.setText("");
        tblMovies.clearSelection();
    }

    // =========================================================================
    // 8. EVENT HANDLERS & LOGIC - CUSTOMERS
    // =========================================================================
    private void handleAddCustomer() {
        String name = txtCustName.getText().trim();
        String mobile = txtCustMobile.getText().trim();
        String email = txtCustEmail.getText().trim();

        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer fields (Name, Mobile, Email)!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer c = new Customer(name, mobile, email);
        boolean success = customerDAO.addCustomer(c);
        if (success) {
            JOptionPane.showMessageDialog(this, "Customer registered successfully with ID: " + c.getCustomerId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearCustomerForm();
            refreshAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer to MySQL database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateCustomer() {
        String idStr = txtCustId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to update!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtCustName.getText().trim();
        String mobile = txtCustMobile.getText().trim();
        String email = txtCustEmail.getText().trim();

        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Customer c = new Customer(id, name, mobile, email);
            boolean success = customerDAO.updateCustomer(c);
            if (success) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCustomerForm();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update customer in MySQL.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Customer ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteCustomer() {
        String idStr = txtCustId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Customer ID #" + idStr + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = customerDAO.deleteCustomer(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearCustomerForm();
                    refreshAllData();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete customer because bookings are registered under this customer ID.\n" +
                        "Cancel their bookings first.",
                        "Constraint Violation", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Customer ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearchCustomer() {
        String query = txtCustSearch.getText().trim();
        if (query.isEmpty()) {
            refreshCustomerTable();
            return;
        }
        List<Customer> list = customerDAO.searchCustomers(query);
        populateCustomerTable(list);
    }

    private void clearCustomerForm() {
        txtCustId.setText("");
        txtCustName.setText("");
        txtCustMobile.setText("");
        txtCustEmail.setText("");
        tblCustomers.clearSelection();
    }

    // =========================================================================
    // 9. EVENT HANDLERS & LOGIC - BOOKINGS
    // =========================================================================
    private void updateBookingPriceAndTotal() {
        ComboItem selectedMovie = (ComboItem) cmbBookingMovie.getSelectedItem();
        if (selectedMovie != null && selectedMovie.getId() > 0) {
            Movie m = movieDAO.getMovieById(selectedMovie.getId());
            if (m != null) {
                double price = m.getTicketPrice();
                txtBookingPrice.setText(String.format("%.2f", price));
                int tickets = (Integer) spnBookingTickets.getValue();
                double total = tickets * price;
                txtBookingTotal.setText(String.format("%.2f", total));
                return;
            }
        }
        txtBookingPrice.setText("0.00");
        txtBookingTotal.setText("0.00");
    }

    private void handleBookTicket() {
        ComboItem custItem = (ComboItem) cmbBookingCustomer.getSelectedItem();
        ComboItem movieItem = (ComboItem) cmbBookingMovie.getSelectedItem();

        if (custItem == null || custItem.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select an existing customer!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (movieItem == null || movieItem.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select an existing movie!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tickets = (Integer) spnBookingTickets.getValue();
        if (tickets <= 0) {
            JOptionPane.showMessageDialog(this, "Number of tickets must be greater than zero!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String showDate = txtBookingDate.getText().trim();
        if (showDate.isEmpty() || !showDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Show Date in YYYY-MM-DD format (e.g. " + LocalDate.now() + ")!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String showTime = (cmbBookingTime.getSelectedItem() != null) ? cmbBookingTime.getSelectedItem().toString() : "10:00 AM";
        String seatNumber = txtBookingSeat.getText().trim();

        if (seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter seat number(s) (e.g. A1, A2)!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate seat availability in MySQL
        boolean alreadyBooked = bookingDAO.isSeatBooked(movieItem.getId(), showDate, showTime, seatNumber);
        if (alreadyBooked) {
            JOptionPane.showMessageDialog(this,
                    "One or more requested seat(s) [" + seatNumber + "] are ALREADY BOOKED for this movie on " + showDate + " at " + showTime + "!\n" +
                    "Please select different seats.",
                    "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch fresh ticket price from database
        Movie movie = movieDAO.getMovieById(movieItem.getId());
        if (movie == null) {
            JOptionPane.showMessageDialog(this, "Selected movie does not exist in the database!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double ticketPrice = movie.getTicketPrice();
        double totalAmount = tickets * ticketPrice;

        Booking booking = new Booking(
                custItem.getId(),
                movieItem.getId(),
                tickets,
                ticketPrice,
                totalAmount,
                showDate,
                showTime,
                seatNumber,
                "Confirmed"
        );

        boolean success = bookingDAO.addBooking(booking);
        if (success) {
            // Display Booking Confirmation Dialog
            String confirmationMsg = "========================================\n" +
                                     "        🎉 TICKET BOOKING CONFIRMED!\n" +
                                     "========================================\n" +
                                     "Booking ID:     #" + booking.getBookingId() + "\n" +
                                     "Customer:       " + custItem.getLabel() + "\n" +
                                     "Movie:          " + movie.getMovieName() + " (" + movie.getLanguage() + ")\n" +
                                     "Show Date:      " + showDate + "\n" +
                                     "Show Time:      " + showTime + "\n" +
                                     "Seat Number(s): " + seatNumber + "\n" +
                                     "Tickets:        " + tickets + "\n" +
                                     "Ticket Price:   Rs. " + String.format("%.2f", ticketPrice) + "\n" +
                                     "Total Paid:     Rs. " + String.format("%.2f", totalAmount) + "\n" +
                                     "Status:         Confirmed\n" +
                                     "========================================\n" +
                                     "Data has been saved into MySQL table 'bookings'.";

            JOptionPane.showMessageDialog(this, confirmationMsg, "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
            clearBookingForm();
            refreshAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save booking to MySQL database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelBooking() {
        String idStr = txtBookingId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to cancel!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int bookingId = Integer.parseInt(idStr);
            Booking b = bookingDAO.getBookingById(bookingId);
            if (b == null) {
                JOptionPane.showMessageDialog(this, "Booking ID #" + bookingId + " not found in database!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("Cancelled".equalsIgnoreCase(b.getBookingStatus())) {
                JOptionPane.showMessageDialog(this, "Booking #" + bookingId + " is ALREADY Cancelled!",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to CANCEL Booking #" + bookingId + " for " + b.getCustomerName() + "?\n" +
                    "This will update the booking status to 'Cancelled' and deduct Rs. " + b.getTotalAmount() + " from total revenue.",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = bookingDAO.cancelBooking(bookingId);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Booking #" + bookingId + " has been successfully CANCELLED!\n" +
                            "Seat(s) [" + b.getSeatNumber() + "] are now released for other customers.",
                            "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    clearBookingForm();
                    refreshAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update booking status in database.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Booking ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchBooking() {
        String query = txtBookingSearch.getText().trim();
        if (query.isEmpty()) {
            refreshBookingTable();
            return;
        }
        List<Booking> list = bookingDAO.searchBookings(query);
        populateBookingTable(list);
    }

    private void clearBookingForm() {
        txtBookingId.setText("");
        if (cmbBookingCustomer.getItemCount() > 0) cmbBookingCustomer.setSelectedIndex(0);
        if (cmbBookingMovie.getItemCount() > 0) cmbBookingMovie.setSelectedIndex(0);
        spnBookingTickets.setValue(1);
        txtBookingDate.setText(LocalDate.now().toString());
        cmbBookingTime.setSelectedIndex(0);
        txtBookingSeat.setText("");
        updateBookingPriceAndTotal();
        tblBookings.clearSelection();
    }

    // =========================================================================
    // 10. REFRESH & DATA POPULATION
    // =========================================================================
    public void refreshAllData() {
        checkAndDisplayDbStatus();
        refreshDashboard();
        refreshMovieTable();
        refreshCustomerTable();
        refreshBookingTable();
        populateBookingDropdowns();
    }

    private void checkAndDisplayDbStatus() {
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            statusLabel.setText("● Connected to MySQL Database [movie_ticket_db] | Host: localhost:3306 | User: " + DatabaseConnection.getUser());
            statusLabel.setForeground(new Color(16, 185, 129));
        } else {
            statusLabel.setText("● Disconnected from MySQL Database! Please verify MySQL service and credentials.");
            statusLabel.setForeground(COLOR_DANGER);
        }
    }

    private void refreshDashboard() {
        int totalMovies = movieDAO.getTotalMoviesCount();
        int totalCustomers = customerDAO.getTotalCustomersCount();
        int totalBookings = bookingDAO.getTotalBookingsCount();
        double totalRevenue = bookingDAO.getTotalRevenue();

        lblTotalMoviesVal.setText(String.valueOf(totalMovies));
        lblTotalCustomersVal.setText(String.valueOf(totalCustomers));
        lblTotalBookingsVal.setText(String.valueOf(totalBookings));
        lblTotalRevenueVal.setText("Rs. " + CURRENCY_FORMAT.format(totalRevenue));

        // Refresh Recent Bookings Table
        modelRecentBookings.setRowCount(0);
        List<Booking> all = bookingDAO.getAllBookings();
        int limit = Math.min(all.size(), 8);
        for (int i = 0; i < limit; i++) {
            Booking b = all.get(i);
            modelRecentBookings.addRow(new Object[]{
                    b.getBookingId(),
                    b.getCustomerName(),
                    b.getMovieName(),
                    b.getNumberOfTickets(),
                    "Rs. " + CURRENCY_FORMAT.format(b.getTotalAmount()),
                    b.getBookingDate(),
                    b.getShowTime(),
                    b.getSeatNumber(),
                    b.getBookingStatus()
            });
        }
    }

    private void refreshMovieTable() {
        List<Movie> list = movieDAO.getAllMovies();
        populateMovieTable(list);
    }

    private void populateMovieTable(List<Movie> list) {
        modelMovies.setRowCount(0);
        for (Movie m : list) {
            modelMovies.addRow(new Object[]{
                    m.getMovieId(),
                    m.getMovieName(),
                    m.getGenre(),
                    m.getDuration(),
                    m.getLanguage(),
                    CURRENCY_FORMAT.format(m.getTicketPrice())
            });
        }
    }

    private void refreshCustomerTable() {
        List<Customer> list = customerDAO.getAllCustomers();
        populateCustomerTable(list);
    }

    private void populateCustomerTable(List<Customer> list) {
        modelCustomers.setRowCount(0);
        for (Customer c : list) {
            modelCustomers.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getCustomerName(),
                    c.getMobile(),
                    c.getEmail()
            });
        }
    }

    private void refreshBookingTable() {
        List<Booking> list = bookingDAO.getAllBookings();
        populateBookingTable(list);
    }

    private void populateBookingTable(List<Booking> list) {
        modelBookings.setRowCount(0);
        for (Booking b : list) {
            modelBookings.addRow(new Object[]{
                    b.getBookingId(),
                    b.getCustomerName() + " (#" + b.getCustomerId() + ")",
                    b.getMovieName() + " (#" + b.getMovieId() + ")",
                    b.getNumberOfTickets(),
                    CURRENCY_FORMAT.format(b.getTicketPrice()),
                    CURRENCY_FORMAT.format(b.getTotalAmount()),
                    b.getBookingDate(),
                    b.getShowTime(),
                    b.getSeatNumber(),
                    b.getBookingStatus()
            });
        }
    }

    private void populateBookingDropdowns() {
        // Populate Customers Combo
        cmbBookingCustomer.removeAllItems();
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer c : customers) {
            cmbBookingCustomer.addItem(new ComboItem(c.getCustomerId(), c.getCustomerName() + " (" + c.getMobile() + ")"));
        }

        // Populate Movies Combo
        cmbBookingMovie.removeAllItems();
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            cmbBookingMovie.addItem(new ComboItem(m.getMovieId(), m.getMovieName() + " [" + m.getLanguage() + " - Rs. " + m.getTicketPrice() + "]"));
        }

        updateBookingPriceAndTotal();
    }

    // =========================================================================
    // 11. UI STYLING HELPERS
    // =========================================================================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(7, 12, 7, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(COLOR_TEXT_DARK);
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_DARK);
        header.setReorderingAllowed(false);
    }

    /**
     * Helper item for JComboBox binding ID with Display Label.
     */
    private static class ComboItem {
        private final int id;
        private final String label;

        public ComboItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() { return id; }
        public String getLabel() { return label; }

        @Override
        public String toString() {
            return label;
        }
    }
}
