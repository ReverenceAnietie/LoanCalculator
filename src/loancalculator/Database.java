package loancalculator;

import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/loan_calculator";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Admin";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(64) NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    profile_pic BYTEA,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            String createCalculationsTable = """
                CREATE TABLE IF NOT EXISTS loan_calculations (
                    id SERIAL PRIMARY KEY,
                    user_id INTEGER REFERENCES users(id),
                    principal DECIMAL(15,2) NOT NULL,
                    annual_rate DECIMAL(5,4) NOT NULL,
                    term_months INTEGER NOT NULL,
                    monthly_payment DECIMAL(15,2) NOT NULL,
                    total_payment DECIMAL(15,2) NOT NULL,
                    total_interest DECIMAL(15,2) NOT NULL,
                    calculation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            Statement stmt = conn.createStatement();
            stmt.execute(createUsersTable);
            stmt.execute(createCalculationsTable);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static boolean registerUser(String username, String password, String email, String fullName) {
        String sql = "INSERT INTO users (username, password_hash, email, full_name) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.toLowerCase());
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    public static int authenticateUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE LOWER(username) = LOWER(?) AND password_hash = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.toLowerCase());
            pstmt.setString(2, hashPassword(password));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            System.err.println("Authentication failed: " + e.getMessage());
        }
        
        return -1;
    }
    
    public static boolean saveCalculation(int userId, double principal, double annualRate, 
                                        int termMonths, double monthlyPayment, 
                                        double totalPayment, double totalInterest) {
        String sql = """
            INSERT INTO loan_calculations 
            (user_id, principal, annual_rate, term_months, monthly_payment, total_payment, total_interest) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, principal);
            pstmt.setDouble(3, annualRate);
            pstmt.setInt(4, termMonths);
            pstmt.setDouble(5, monthlyPayment);
            pstmt.setDouble(6, totalPayment);
            pstmt.setDouble(7, totalInterest);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Failed to save calculation: " + e.getMessage());
            return false;
        }
    }
    
    public static String getUserFullName(int userId) {
        String sql = "SELECT full_name FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name");
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to get user name: " + e.getMessage());
        }
        
        return "User";
    }
    
    public static String getUsername(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("username");
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to get username: " + e.getMessage());
        }
        
        return null;
    }
    
    public static boolean saveProfilePicture(String username, BufferedImage image) {
        String sql = "UPDATE users SET profile_pic = ? WHERE LOWER(username) = LOWER(?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            pstmt.setBytes(1, imageBytes);
            pstmt.setString(2, username.toLowerCase());
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException | IOException e) {
            System.err.println("Error saving profile picture: " + e.getMessage());
            return false;
        }
    }
    
    public static BufferedImage getProfilePicture(String username) {
        String sql = "SELECT profile_pic FROM users WHERE LOWER(username) = LOWER(?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("profile_pic");
                if (imageBytes != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    return ImageIO.read(bais);
                }
            }
            return null;
        } catch (SQLException | IOException e) {
            System.err.println("Error retrieving profile picture: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean removeProfilePicture(String username) {
        String sql = "UPDATE users SET profile_pic = NULL WHERE LOWER(username) = LOWER(?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.toLowerCase());
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error removing profile picture: " + e.getMessage());
            return false;
        }
    }
}