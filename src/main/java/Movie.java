/**
 * Movie.java
 * Entity model representing a Movie in the system.
 */
public class Movie {
    private int movieId;
    private String movieName;
    private String genre;
    private int duration; // in minutes
    private String language;
    private double ticketPrice;

    public Movie() {
    }

    // Constructor without ID (used for inserting new movie)
    public Movie(String movieName, String genre, int duration, String language, double ticketPrice) {
        this.movieName = movieName;
        this.genre = genre;
        this.duration = duration;
        this.language = language;
        this.ticketPrice = ticketPrice;
    }

    // Constructor with ID (used when fetching from database)
    public Movie(int movieId, String movieName, String genre, int duration, String language, double ticketPrice) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.genre = genre;
        this.duration = duration;
        this.language = language;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "[ID: " + movieId + "] " + movieName + " (" + language + " - Rs. " + ticketPrice + ")";
    }
}
