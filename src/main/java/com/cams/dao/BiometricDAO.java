package com.cams.dao;

import com.cams.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BiometricDAO {

    public BiometricDAO() {
        createTableIfNotExists();
    }

    private synchronized void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS user_biometrics (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "user_id VARCHAR(50) NOT NULL, " +
                     "user_type VARCHAR(20) NOT NULL, " +
                     "credential_id VARCHAR(500) NOT NULL, " +
                     "public_key TEXT, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "UNIQUE KEY unique_user_bio (user_id, user_type)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
        } catch (Exception e) {
            System.err.println("BiometricDAO Table Creation Warning: " + e.getMessage());
        }
    }

    public boolean saveBiometric(String userId, String userType, String credentialId, String publicKey) {
        if (userId == null || userType == null || credentialId == null) return false;
        createTableIfNotExists();
        String sql = "INSERT INTO user_biometrics (user_id, user_type, credential_id, public_key) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE credential_id = VALUES(credential_id), public_key = VALUES(public_key)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId.trim());
                ps.setString(2, userType.trim());
                ps.setString(3, credentialId.trim());
                ps.setString(4, publicKey != null ? publicKey.trim() : "");
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBiometricRegistered(String userId, String userType) {
        if (userId == null || userType == null) return false;
        String sql = "SELECT id FROM user_biometrics WHERE user_id = ? AND user_type = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId.trim());
                ps.setString(2, userType.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] findUserByCredentialId(String credentialId) {
        if (credentialId == null || credentialId.trim().isEmpty()) return null;
        String sql = "SELECT user_id, user_type FROM user_biometrics WHERE credential_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, credentialId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{ rs.getString("user_id"), rs.getString("user_type") };
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean deleteBiometric(String userId, String userType) {
        if (userId == null || userType == null) return false;
        String sql = "DELETE FROM user_biometrics WHERE user_id = ? AND user_type = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId.trim());
                ps.setString(2, userType.trim());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
