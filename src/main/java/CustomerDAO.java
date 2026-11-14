import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO.java
 * Data Access Object for handling CRUD operations on the 'customers' table in MySQL.
 */
public class CustomerDAO {

    /**
     * Inserts a new customer into the database.
     * @param customer Customer entity to insert
     * @return true if successful, false otherwise
     */
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, mobile, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getMobile());
            ps.setString(3, customer.getEmail());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        customer.setCustomerId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing customer's details.
     * @param customer Customer entity with updated details
     * @return true if updated, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name = ?, mobile = ?, email = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getMobile());
            ps.setString(3, customer.getEmail());
            ps.setInt(4, customer.getCustomerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a customer by ID.
     * @param customerId ID of customer to delete
     * @return true if deleted
     * @throws SQLException if foreign key restricts deletion
     */
    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Retrieves a customer by their ID.
     * @param customerId ID of customer
     * @return Customer entity, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching customer by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all customers from the database.
     * @return List of Customer objects
     */
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching all customers: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches customers by keyword (matches name, mobile, email, or ID).
     * @param keyword Search term
     * @return List of matching Customer objects
     */
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_name LIKE ? OR mobile LIKE ? OR email LIKE ? OR CAST(customer_id AS CHAR) LIKE ? ORDER BY customer_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword.trim() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractCustomerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error searching customers: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns total count of customers in database.
     */
    public int getTotalCustomersCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error counting customers: " + e.getMessage());
        }
        return 0;
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("customer_name"),
            rs.getString("mobile"),
            rs.getString("email")
        );
    }
}
