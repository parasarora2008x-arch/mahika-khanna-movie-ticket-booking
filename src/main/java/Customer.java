/**
 * Customer.java
 * Entity model representing a Customer in the system.
 */
public class Customer {
    private int customerId;
    private String customerName;
    private String mobile;
    private String email;

    public Customer() {
    }

    // Constructor without ID (used for inserting new customer)
    public Customer(String customerName, String mobile, String email) {
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
    }

    // Constructor with ID (used when fetching from database)
    public Customer(int customerId, String customerName, String mobile, String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "[ID: " + customerId + "] " + customerName + " (" + mobile + ")";
    }
}
