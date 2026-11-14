/**
 * Booking.java
 * Entity model representing a Ticket Booking in the system.
 */
public class Booking {
    private int bookingId;
    private int customerId;
    private int movieId;
    private int numberOfTickets;
    private double ticketPrice;
    private double totalAmount;
    private String bookingDate; // Format: YYYY-MM-DD
    private String showTime;
    private String seatNumber;
    private String bookingStatus; // "Confirmed", "Cancelled"

    // Optional display fields populated from JOIN queries
    private String customerName;
    private String movieName;

    public Booking() {
        this.bookingStatus = "Confirmed";
    }

    // Constructor without bookingId (used when creating a new booking)
    public Booking(int customerId, int movieId, int numberOfTickets, double ticketPrice,
                   double totalAmount, String bookingDate, String showTime,
                   String seatNumber, String bookingStatus) {
        this.customerId = customerId;
        this.movieId = movieId;
        this.numberOfTickets = numberOfTickets;
        this.ticketPrice = ticketPrice;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.bookingStatus = (bookingStatus != null && !bookingStatus.trim().isEmpty()) ? bookingStatus : "Confirmed";
    }

    // Constructor with bookingId (used when loading from database)
    public Booking(int bookingId, int customerId, int movieId, int numberOfTickets, double ticketPrice,
                   double totalAmount, String bookingDate, String showTime,
                   String seatNumber, String bookingStatus) {
        this(customerId, movieId, numberOfTickets, ticketPrice, totalAmount, bookingDate, showTime, seatNumber, bookingStatus);
        this.bookingId = bookingId;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " [CustID=" + customerId + ", MovieID=" + movieId + 
               ", Tickets=" + numberOfTickets + ", Total=Rs. " + totalAmount + ", Status=" + bookingStatus + "]";
    }
}
