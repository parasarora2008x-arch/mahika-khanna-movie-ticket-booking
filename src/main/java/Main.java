import javax.swing.*;
import java.awt.*;

/**
 * Main.java
 * Application entry point for the Movie Ticket Booking Management System.
 * 
 * College Practical Project: Movie Ticket Booking Management System
 * Stack: Core Java, Java Swing, MySQL 8.0, JDBC, Maven
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   🎬 MOVIE TICKET BOOKING MANAGEMENT SYSTEM (JAVA + MYSQL)       ");
        System.out.println("=================================================================");
        System.out.println("[Main] Initializing system...");

        // Set modern System Look and Feel for native desktop aesthetics
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Enhance default font for crisp rendering
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        } catch (Exception e) {
            System.err.println("[Main] Could not set System Look and Feel, using default: " + e.getMessage());
        }

        // Test Database Connectivity prior to launching UI
        System.out.println("[Main] Testing JDBC connection to MySQL database: " + DatabaseConnection.getUrl());
        boolean dbConnected = DatabaseConnection.testConnection();

        if (dbConnected) {
            System.out.println("[Main] SUCCESS: Connected to MySQL database [movie_ticket_db] successfully!");
        } else {
            System.err.println("[Main] WARNING: Unable to establish MySQL database connection on startup.");
            System.err.println("[Main] Please ensure MySQL Server is running and credentials in DatabaseConnection.java or db.properties are correct.");
        }

        // Start built-in Web Portal Server on http://localhost:8080
        WebServer.startServer(8080);

        // Launch GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                System.out.println("[Main] Graphical User Interface (GUI) loaded successfully!");
            } catch (Exception e) {
                System.err.println("[Main] Fatal error launching application frame: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
