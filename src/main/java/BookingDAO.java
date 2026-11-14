import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BookingDAO.java
 * Data Access Object for handling Ticket Booking operations in MySQL.
 */
public class BookingDAO {

    /**
     * Inserts a new booking into the database.
     * @param booking Booking entity to add
     * @return true if successful, false otherwise
     */
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, movie_id, number_of_tickets, ticket_price, total_amount, "
                   + "booking_date, show_time, seat_number, booking_status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Ensure total amount matches formula: tickets * ticketPrice
        double calculatedTotal = booking.getNumberOfTickets() * booking.getTicketPrice();
        booking.setTotalAmount(calculatedTotal);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getMovieId());
            ps.setInt(3, booking.getNumberOfTickets());
            ps.setDouble(4, booking.getTicketPrice());
            ps.setDouble(5, booking.getTotalAmount());
            ps.setDate(6, Date.valueOf(booking.getBookingDate())); // Expecting YYYY-MM-DD
            ps.setString(7, booking.getShowTime());
            ps.setString(8, booking.getSeatNumber().trim());
            ps.setString(9, (booking.getBookingStatus() != null) ? booking.getBookingStatus() : "Confirmed");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setBookingId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error adding booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cancels a booking by updating its status to 'Cancelled'.
     * Requirement: Cancel booking should update booking_status to Cancelled instead of deleting.
     * @param bookingId ID of booking to cancel
     * @return true if status was updated, false otherwise
     */
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET booking_status = 'Cancelled' WHERE booking_id = ? AND booking_status != 'Cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if any of the specified seat numbers are already booked for the given movie, date, and time.
     * Only non-cancelled bookings are considered occupied.
     * @param movieId Movie ID
     * @param showDate Date string in YYYY-MM-DD format
     * @param showTime Show time string
     * @param seatInput Comma-separated or single seat numbers (e.g., "A1" or "A1, A2")
     * @return true if ANY requested seat is already booked, false if all are available
     */
    public boolean isSeatBooked(int movieId, String showDate, String showTime, String seatInput) {
        if (seatInput == null || seatInput.trim().isEmpty()) {
            return false;
        }

        // Split requested seats into normalized lowercase tokens
        String[] requestedSeats = seatInput.split("[,;\\s]+");
        Set<String> requestedSet = new HashSet<>();
        for (String s : requestedSeats) {
            if (!s.trim().isEmpty()) {
                requestedSet.add(s.trim().toUpperCase());
            }
        }

        String sql = "SELECT seat_number FROM bookings "
                   + "WHERE movie_id = ? AND booking_date = ? AND show_time = ? AND booking_status != 'Cancelled'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setDate(2, Date.valueOf(showDate));
            ps.setString(3, showTime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String existingSeatsStr = rs.getString("seat_number");
                    if (existingSeatsStr != null) {
                        String[] existingSeats = existingSeatsStr.split("[,;\\s]+");
                        for (String es : existingSeats) {
                            if (requestedSet.contains(es.trim().toUpperCase())) {
                                // Conflict found
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error checking seat availability: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a single booking by ID with customer name and movie name.
     */
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, c.customer_name, m.movie_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "JOIN movies m ON b.movie_id = m.movie_id "
                   + "WHERE b.booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBookingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error fetching booking by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all bookings with customer and movie details, ordered by most recent first.
     */
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.customer_name, m.movie_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "JOIN movies m ON b.movie_id = m.movie_id "
                   + "ORDER BY b.booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error fetching all bookings: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches bookings by keyword across booking ID, customer name, movie name, seat number, or status.
     */
    public List<Booking> searchBookings(String keyword) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.customer_name, m.movie_name "
                   + "FROM bookings b "
                   + "JOIN customers c ON b.customer_id = c.customer_id "
                   + "JOIN movies m ON b.movie_id = m.movie_id "
                   + "WHERE CAST(b.booking_id AS CHAR) LIKE ? "
                   + "   OR c.customer_name LIKE ? "
                   + "   OR m.movie_name LIKE ? "
                   + "   OR b.seat_number LIKE ? "
                   + "   OR b.booking_status LIKE ? "
                   + "ORDER BY b.booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword.trim() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractBookingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error searching bookings: " + e.getMessage());
        }
        return list;
    }

    /**
     * Total count of all bookings recorded.
     */
    public int getTotalBookingsCount() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error counting bookings: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Total revenue calculated from Confirmed bookings only.
     * Requirement: Cancelled bookings should not be counted in revenue.
     */
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings WHERE booking_status != 'Cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Error calculating revenue: " + e.getMessage());
        }
        return 0.0;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking b = new Booking(
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("movie_id"),
            rs.getInt("number_of_tickets"),
            rs.getDouble("ticket_price"),
            rs.getDouble("total_amount"),
            rs.getDate("booking_date").toString(),
            rs.getString("show_time"),
            rs.getString("seat_number"),
            rs.getString("booking_status")
        );
        b.setCustomerName(rs.getString("customer_name"));
        b.setMovieName(rs.getString("movie_name"));
        return b;
    }
}
