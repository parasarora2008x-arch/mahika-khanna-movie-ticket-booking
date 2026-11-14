import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebServer.java
 * Pure Core Java HTTP Server (built-in jdk.httpserver, no external frameworks).
 * Serves the Movie Ticket Booking Management Web Portal at http://localhost:8080.
 * Directly integrates with MySQL database (movie_ticket_db) via DAOs.
 */
public class WebServer {

    private static final int PORT = 8080;
    private static final MovieDAO movieDAO = new MovieDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    public static void main(String[] args) {
        startServer(PORT);
        try {
            // Keep main thread alive for standalone execution
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {}
    }

    public static HttpServer startServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new IndexHandler());
            server.createContext("/api/data", new ApiDataHandler());
            server.createContext("/api/book", new ApiBookHandler());
            server.createContext("/api/cancel", new ApiCancelHandler());
            server.setExecutor(null); // default executor
            server.start();
            System.out.println("=================================================================");
            System.out.println("   🌐 WEB PORTAL LIVE: http://localhost:" + port + "           ");
            System.out.println("   Connected to MySQL Database: " + DatabaseConnection.getUrl());
            System.out.println("=================================================================");
            return server;
        } catch (IOException e) {
            System.err.println("[WebServer] Failed to start HTTP server on port " + port + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Handler for Main Web Page (HTML + CSS + JS)
     */
    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String html = generateHtmlPage();
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    /**
     * Handler returning live data as JSON
     */
    static class ApiDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("{");

            // Stats
            json.append("\"stats\":{");
            json.append("\"totalMovies\":").append(movieDAO.getTotalMoviesCount()).append(",");
            json.append("\"totalCustomers\":").append(customerDAO.getTotalCustomersCount()).append(",");
            json.append("\"totalBookings\":").append(bookingDAO.getTotalBookingsCount()).append(",");
            json.append("\"totalRevenue\":").append(String.format("%.2f", bookingDAO.getTotalRevenue()));
            json.append("},");

            // Movies
            json.append("\"movies\":[");
            List<Movie> movies = movieDAO.getAllMovies();
            for (int i = 0; i < movies.size(); i++) {
                Movie m = movies.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"id\":").append(m.getMovieId()).append(",")
                    .append("\"name\":\"").append(escapeJson(m.getMovieName())).append("\",")
                    .append("\"genre\":\"").append(escapeJson(m.getGenre())).append("\",")
                    .append("\"duration\":").append(m.getDuration()).append(",")
                    .append("\"language\":\"").append(escapeJson(m.getLanguage())).append("\",")
                    .append("\"price\":").append(m.getTicketPrice())
                    .append("}");
            }
            json.append("],");

            // Customers
            json.append("\"customers\":[");
            List<Customer> customers = customerDAO.getAllCustomers();
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"id\":").append(c.getCustomerId()).append(",")
                    .append("\"name\":\"").append(escapeJson(c.getCustomerName())).append("\",")
                    .append("\"mobile\":\"").append(escapeJson(c.getMobile())).append("\",")
                    .append("\"email\":\"").append(escapeJson(c.getEmail())).append("\"")
                    .append("}");
            }
            json.append("],");

            // Bookings
            json.append("\"bookings\":[");
            List<Booking> bookings = bookingDAO.getAllBookings();
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"id\":").append(b.getBookingId()).append(",")
                    .append("\"customer\":\"").append(escapeJson(b.getCustomerName())).append("\",")
                    .append("\"movie\":\"").append(escapeJson(b.getMovieName())).append("\",")
                    .append("\"tickets\":").append(b.getNumberOfTickets()).append(",")
                    .append("\"price\":").append(b.getTicketPrice()).append(",")
                    .append("\"total\":").append(b.getTotalAmount()).append(",")
                    .append("\"date\":\"").append(b.getBookingDate()).append("\",")
                    .append("\"time\":\"").append(escapeJson(b.getShowTime())).append("\",")
                    .append("\"seat\":\"").append(escapeJson(b.getSeatNumber())).append("\",")
                    .append("\"status\":\"").append(escapeJson(b.getBookingStatus())).append("\"")
                    .append("}");
            }
            json.append("]}");

            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    /**
     * Handler for Booking Ticket via Web Form
     */
    static class ApiBookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, String> params = parseFormData(exchange);
            try {
                int custId = Integer.parseInt(params.get("customerId"));
                int movieId = Integer.parseInt(params.get("movieId"));
                int tickets = Integer.parseInt(params.get("tickets"));
                String date = params.get("date");
                String time = params.get("time");
                String seat = params.get("seat");

                if (tickets <= 0 || seat == null || seat.trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Invalid tickets or seat number!\"}");
                    return;
                }

                // Check seat availability
                if (bookingDAO.isSeatBooked(movieId, date, time, seat)) {
                    sendJsonResponse(exchange, 409, "{\"success\":false,\"message\":\"Seat(s) [" + seat + "] are ALREADY BOOKED for this show!\"}");
                    return;
                }

                Movie movie = movieDAO.getMovieById(movieId);
                if (movie == null) {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Movie not found in database!\"}");
                    return;
                }

                double price = movie.getTicketPrice();
                double total = tickets * price;

                Booking b = new Booking(custId, movieId, tickets, price, total, date, time, seat, "Confirmed");
                boolean ok = bookingDAO.addBooking(b);
                if (ok) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"bookingId\":" + b.getBookingId() + ",\"total\":" + total + "}");
                } else {
                    sendJsonResponse(exchange, 500, "{\"success\":false,\"message\":\"Database insertion failed!\"}");
                }
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handler for Cancelling Booking via Web
     */
    static class ApiCancelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, String> params = parseFormData(exchange);
            try {
                int bookingId = Integer.parseInt(params.get("bookingId"));
                boolean ok = bookingDAO.cancelBooking(bookingId);
                if (ok) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Booking #" + bookingId + " cancelled successfully!\"}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Could not cancel booking or already cancelled.\"}");
                }
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(k, v);
            }
        }
        return map;
    }

    private static void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    /**
     * Generates modern, cinema-themed HTML Web Portal
     */
    private static String generateHtmlPage() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🎬 CineBook | Movie Ticket Booking Management System</title>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #000000;
            --primary-hover: #1e293b;
            --bg-body: #f8fafc;
            --card-bg: #ffffff;
            --card-border: #e2e8f0;
            --text-main: #09090b;
            --text-muted: #64748b;
            --accent-green: #15803d;
            --accent-red: #b91c1c;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.05);
            --shadow-md: 0 4px 16px rgba(0,0,0,0.07);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Outfit', sans-serif; }
        body { background: var(--bg-body); color: var(--text-main); line-height: 1.5; padding-bottom: 60px; }
        
        /* Navbar */
        nav { background: #ffffff; border-bottom: 1px solid var(--card-border); padding: 16px 40px; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 100; box-shadow: var(--shadow-sm); }
        .logo { font-size: 22px; font-weight: 800; color: #000000; display: flex; align-items: center; gap: 8px; letter-spacing: -0.5px; }
        .logo span { color: #000000; text-decoration: underline; text-decoration-color: #3b82f6; text-underline-offset: 4px; }
        .badge-live { background: #ecfdf5; color: #047857; font-size: 12px; font-weight: 600; padding: 4px 12px; border-radius: 20px; border: 1px solid #a7f3d0; }

        /* Container */
        .container { max-width: 1200px; margin: 0 auto; padding: 36px 20px; }
        
        /* Hero */
        .hero { text-align: center; margin-bottom: 40px; }
        .hero h1 { font-size: 38px; font-weight: 800; margin-bottom: 10px; color: #000000; letter-spacing: -0.5px; }
        .hero p { color: var(--text-muted); font-size: 16px; font-weight: 400; }

        /* Stats Grid */
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 40px; }
        .stat-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 14px; padding: 22px; display: flex; justify-content: space-between; align-items: center; box-shadow: var(--shadow-sm); transition: transform 0.2s, box-shadow 0.2s; }
        .stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
        .stat-card .val { font-size: 28px; font-weight: 800; color: #000000; margin-top: 5px; letter-spacing: -0.5px; }
        .stat-card .title { font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 600; letter-spacing: 0.8px; }
        .stat-icon { font-size: 28px; padding: 10px; border-radius: 12px; background: #f8fafc; border: 1px solid #e2e8f0; }

        /* Section Headings */
        .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; margin-top: 40px; }
        .section-header h2 { font-size: 22px; font-weight: 700; color: #000000; letter-spacing: -0.3px; }

        /* Movies Grid */
        .movie-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 22px; }
        .movie-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 14px; padding: 22px; transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s; display: flex; flex-direction: column; justify-content: space-between; box-shadow: var(--shadow-sm); }
        .movie-card:hover { transform: translateY(-4px); border-color: #000000; box-shadow: var(--shadow-md); }
        .movie-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
        .movie-title { font-size: 18px; font-weight: 700; color: #000000; }
        .movie-genre { display: inline-block; background: #f1f5f9; color: #334155; font-size: 11px; font-weight: 600; padding: 3px 10px; border-radius: 6px; border: 1px solid #e2e8f0; margin-bottom: 12px; }
        .movie-info { font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
        .movie-price { font-size: 22px; font-weight: 800; color: #000000; margin: 15px 0 12px 0; }
        
        /* Buttons */
        .btn { display: inline-block; width: 100%; background: #000000; color: #ffffff; text-align: center; padding: 11px 16px; border-radius: 8px; font-weight: 600; font-size: 14px; border: none; cursor: pointer; transition: background 0.2s; }
        .btn:hover { background: #334155; }
        .btn-cancel { background: #ffffff; color: #dc2626; border: 1px solid #fca5a5; padding: 5px 12px; border-radius: 6px; cursor: pointer; font-size: 12px; font-weight: 600; transition: all 0.2s; }
        .btn-cancel:hover { background: #dc2626; color: #ffffff; }

        /* Table */
        .table-container { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 14px; overflow-x: auto; box-shadow: var(--shadow-sm); }
        table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        th { background: #f8fafc; color: #475569; padding: 14px 18px; font-weight: 600; border-bottom: 2px solid var(--card-border); }
        td { padding: 14px 18px; border-bottom: 1px solid #f1f5f9; color: #0f172a; }
        tr:last-child td { border-bottom: none; }
        code { background: #f1f5f9; border: 1px solid #e2e8f0; padding: 3px 6px; border-radius: 4px; font-family: monospace; color: #000000; font-size: 13px; font-weight: 600; }
        .badge-status { padding: 4px 12px; border-radius: 20px; font-size: 11px; font-weight: 600; }
        .status-confirmed { background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; }
        .status-cancelled { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }

        /* Modal */
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); backdrop-filter: blur(4px); z-index: 1000; align-items: center; justify-content: center; }
        .modal.active { display: flex; }
        .modal-box { background: #ffffff; border: 1px solid var(--card-border); border-radius: 16px; width: 90%; max-width: 480px; padding: 26px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); color: #000000; }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .modal-header h3 { font-size: 20px; font-weight: 700; color: #000000; }
        .close-btn { background: none; border: none; color: #64748b; font-size: 24px; cursor: pointer; }
        .close-btn:hover { color: #000000; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; font-size: 13px; color: #475569; margin-bottom: 6px; font-weight: 600; }
        .form-control { width: 100%; background: #ffffff; border: 1px solid #cbd5e1; color: #000000; padding: 10px 14px; border-radius: 8px; font-size: 14px; outline: none; transition: border-color 0.2s; }
        .form-control:focus { border-color: #000000; }
        .form-control[readonly] { background: #f8fafc; color: #475569; border-color: #e2e8f0; }
        .total-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px; display: flex; justify-content: space-between; align-items: center; margin: 15px 0 20px 0; font-weight: 600; }
        .total-box .amt { font-size: 22px; font-weight: 800; color: #000000; }
    </style>
</head>
<body>

    <nav>
        <div class="logo">🎬 <span>CineBook</span> Portal</div>
        <div class="badge-live">● Connected to MySQL (localhost:3306)</div>
    </nav>

    <div class="container">
        <div class="hero">
            <h1>Movie Ticket Booking Management System</h1>
            <p>Live Web Portal powered by Pure Java + MySQL Database (movie_ticket_db)</p>
        </div>

        <!-- Stats -->
        <div class="stats-grid">
            <div class="stat-card">
                <div>
                    <div class="title">Total Movies</div>
                    <div class="val" id="statMovies">...</div>
                </div>
                <div class="stat-icon">🎬</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="title">Total Customers</div>
                    <div class="val" id="statCustomers">...</div>
                </div>
                <div class="stat-icon">👥</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="title">Total Bookings</div>
                    <div class="val" id="statBookings">...</div>
                </div>
                <div class="stat-icon">🎟️</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="title">Total Revenue</div>
                    <div class="val" id="statRevenue">...</div>
                </div>
                <div class="stat-icon">💰</div>
            </div>
        </div>

        <!-- Movies -->
        <div class="section-header">
            <h2>🎬 Currently Showing Movies</h2>
        </div>
        <div class="movie-grid" id="moviesGrid">
            <!-- Rendered via JS -->
        </div>

        <!-- Live Bookings -->
        <div class="section-header">
            <h2>📋 Live Booking Records (Real MySQL Database)</h2>
        </div>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Booking ID</th>
                        <th>Customer</th>
                        <th>Movie</th>
                        <th>Tickets</th>
                        <th>Price</th>
                        <th>Total Paid</th>
                        <th>Show Time</th>
                        <th>Seats</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="bookingsTable">
                    <!-- Rendered via JS -->
                </tbody>
            </table>
        </div>
    </div>

    <!-- Booking Modal -->
    <div class="modal" id="bookModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3>🎟️ Book Movie Tickets</h3>
                <button class="close-btn" onclick="closeModal()">&times;</button>
            </div>
            <form id="bookForm" onsubmit="handleBookSubmit(event)">
                <input type="hidden" id="modalMovieId">
                <div class="form-group">
                    <label>Selected Movie</label>
                    <input type="text" id="modalMovieName" class="form-control" readonly>
                </div>
                <div class="form-group">
                    <label>Select Customer</label>
                    <select id="modalCustomerSelect" class="form-control" required></select>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label>Tickets Count</label>
                        <input type="number" id="modalTickets" class="form-control" min="1" max="10" value="1" oninput="updateTotal()" required>
                    </div>
                    <div class="form-group">
                        <label>Ticket Price (Rs.)</label>
                        <input type="text" id="modalPrice" class="form-control" readonly>
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                    <div class="form-group">
                        <label>Show Date</label>
                        <input type="date" id="modalDate" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Show Time</label>
                        <select id="modalTime" class="form-control" required>
                            <option>10:00 AM</option>
                            <option>01:15 PM</option>
                            <option>04:30 PM</option>
                            <option>07:45 PM</option>
                            <option>10:30 PM</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label>Seat Number(s) (e.g. A1, A2)</label>
                    <input type="text" id="modalSeat" class="form-control" placeholder="e.g. VIP-1, VIP-2 or A5" required>
                </div>
                <div class="total-box">
                    <span>Total Amount to Pay:</span>
                    <span class="amt" id="modalTotal">Rs. 0.00</span>
                </div>
                <button type="submit" class="btn">Confirm & Save to MySQL</button>
            </form>
        </div>
    </div>

    <script>
        let appData = { movies: [], customers: [], bookings: [], stats: {} };
        let currentMoviePrice = 0;

        // Set default date to today
        document.getElementById('modalDate').value = new Date().toISOString().split('T')[0];

        async function loadData() {
            try {
                const res = await fetch('/api/data');
                appData = await res.json();
                renderStats();
                renderMovies();
                renderBookings();
            } catch (err) {
                console.error("Failed to fetch live data:", err);
            }
        }

        function renderStats() {
            document.getElementById('statMovies').innerText = appData.stats.totalMovies;
            document.getElementById('statCustomers').innerText = appData.stats.totalCustomers;
            document.getElementById('statBookings').innerText = appData.stats.totalBookings;
            document.getElementById('statRevenue').innerText = 'Rs. ' + Number(appData.stats.totalRevenue).toLocaleString();
        }

        function renderMovies() {
            const grid = document.getElementById('moviesGrid');
            grid.innerHTML = appData.movies.map(m => `
                <div class="movie-card">
                    <div>
                        <div class="movie-header">
                            <div class="movie-title">${m.name}</div>
                        </div>
                        <div class="movie-genre">${m.genre}</div>
                        <div class="movie-info">⏱️ Duration: ${m.duration} mins</div>
                        <div class="movie-info">🗣️ Language: ${m.language}</div>
                        <div class="movie-price">Rs. ${m.price.toFixed(2)}</div>
                    </div>
                    <button class="btn" onclick="openBookModal(${m.id})">🎟️ Book Tickets</button>
                </div>
            `).join('');
        }

        function renderBookings() {
            const tbody = document.getElementById('bookingsTable');
            tbody.innerHTML = appData.bookings.map(b => `
                <tr>
                    <td><b>#${b.id}</b></td>
                    <td>${b.customer}</td>
                    <td>${b.movie}</td>
                    <td>${b.tickets}</td>
                    <td>Rs. ${b.price.toFixed(2)}</td>
                    <td><b>Rs. ${b.total.toFixed(2)}</b></td>
                    <td>${b.time}</td>
                    <td><code>${b.seat}</code></td>
                    <td>
                        <span class="badge-status ${b.status === 'Confirmed' ? 'status-confirmed' : 'status-cancelled'}">
                            ${b.status}
                        </span>
                    </td>
                    <td>
                        ${b.status === 'Confirmed' 
                            ? `<button class="btn-cancel" onclick="cancelBooking(${b.id})">Cancel</button>`
                            : `<span style="color:var(--text-muted);font-size:12px;">Cancelled</span>`
                        }
                    </td>
                </tr>
            `).join('');
        }

        function openBookModal(movieId) {
            const movie = appData.movies.find(m => m.id === movieId);
            if (!movie) return;
            document.getElementById('modalMovieId').value = movie.id;
            document.getElementById('modalMovieName').value = movie.name + ' (' + movie.language + ')';
            document.getElementById('modalPrice').value = 'Rs. ' + movie.price.toFixed(2);
            currentMoviePrice = movie.price;
            
            // Populate customers
            const custSelect = document.getElementById('modalCustomerSelect');
            custSelect.innerHTML = appData.customers.map(c => `
                <option value="${c.id}">${c.name} (${c.mobile})</option>
            `).join('');

            updateTotal();
            document.getElementById('bookModal').classList.add('active');
        }

        function updateTotal() {
            const count = parseInt(document.getElementById('modalTickets').value) || 1;
            const total = count * currentMoviePrice;
            document.getElementById('modalTotal').innerText = 'Rs. ' + total.toFixed(2);
        }

        function closeModal() {
            document.getElementById('bookModal').classList.remove('active');
        }

        async function handleBookSubmit(e) {
            e.preventDefault();
            const body = new URLSearchParams({
                customerId: document.getElementById('modalCustomerSelect').value,
                movieId: document.getElementById('modalMovieId').value,
                tickets: document.getElementById('modalTickets').value,
                date: document.getElementById('modalDate').value,
                time: document.getElementById('modalTime').value,
                seat: document.getElementById('modalSeat').value
            });

            try {
                const res = await fetch('/api/book', { method: 'POST', body });
                const json = await res.json();
                if (json.success) {
                    alert('🎉 Booking Confirmed!\\nBooking ID: #' + json.bookingId + '\\nTotal Paid: Rs. ' + json.total + '\\nSaved into MySQL!');
                    closeModal();
                    loadData();
                } else {
                    alert('❌ Error: ' + json.message);
                }
            } catch (err) {
                alert('Network error: ' + err.message);
            }
        }

        async function cancelBooking(bookingId) {
            if (!confirm('Are you sure you want to cancel Booking #' + bookingId + '?\\nThis will free the seats and deduct amount from revenue.')) {
                return;
            }

            const body = new URLSearchParams({ bookingId });
            try {
                const res = await fetch('/api/cancel', { method: 'POST', body });
                const json = await res.json();
                if (json.success) {
                    alert('Booking #' + bookingId + ' has been Cancelled!');
                    loadData();
                } else {
                    alert('Error: ' + json.message);
                }
            } catch (err) {
                alert('Error: ' + err.message);
            }
        }

        // Initial Load and Auto Refresh every 5 seconds
        loadData();
        setInterval(loadData, 5000);
    </script>
</body>
</html>
""";
    }
}
