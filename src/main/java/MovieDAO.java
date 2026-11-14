import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MovieDAO.java
 * Data Access Object for handling CRUD operations on the 'movies' table in MySQL.
 */
public class MovieDAO {

    /**
     * Inserts a new movie into the database.
     * @param movie The Movie object to add
     * @return true if insertion was successful, false otherwise
     */
    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (movie_name, genre, duration, language, ticket_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getDuration());
            ps.setString(4, movie.getLanguage());
            ps.setDouble(5, movie.getTicketPrice());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movie.setMovieId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error adding movie: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing movie in the database.
     * @param movie The Movie object containing updated information
     * @return true if updated successfully, false otherwise
     */
    public boolean updateMovie(Movie movie) {
        String sql = "UPDATE movies SET movie_name = ?, genre = ?, duration = ?, language = ?, ticket_price = ? WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getDuration());
            ps.setString(4, movie.getLanguage());
            ps.setDouble(5, movie.getTicketPrice());
            ps.setInt(6, movie.getMovieId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error updating movie: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a movie by ID.
     * @param movieId ID of the movie to delete
     * @return true if deleted, false if failed or has related bookings
     * @throws SQLException if foreign key constraint restricts deletion
     */
    public boolean deleteMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Re-throw so caller can distinguish FK constraint violations (Error 1451)
            throw e;
        }
    }

    /**
     * Retrieves a single movie by its ID.
     * @param movieId ID of the movie
     * @return Movie object, or null if not found
     */
    public Movie getMovieById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMovieFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error fetching movie by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all movies from the database.
     * @return List of Movie objects
     */
    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY movie_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error fetching all movies: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches movies by keyword (matches name, genre, or language).
     * @param keyword Search string
     * @return List of matching Movie objects
     */
    public List<Movie> searchMovies(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE movie_name LIKE ? OR genre LIKE ? OR language LIKE ? OR CAST(movie_id AS CHAR) LIKE ? ORDER BY movie_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword.trim() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMovieFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error searching movies: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns total count of movies in database.
     */
    public int getTotalMoviesCount() {
        String sql = "SELECT COUNT(*) FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] Error counting movies: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper to map a ResultSet row to a Movie object.
     */
    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("movie_id"),
            rs.getString("movie_name"),
            rs.getString("genre"),
            rs.getInt("duration"),
            rs.getString("language"),
            rs.getDouble("ticket_price")
        );
    }
}
