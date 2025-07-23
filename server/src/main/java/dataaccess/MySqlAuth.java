package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;
import java.sql.*;

public class MySqlAuth implements IAuthDAO {
    @Override
    public String createAuth(String username) throws DataAccessException {

        String newAuthToken = createAuthToken();

        if (username == null || username.isBlank()) {
            throw new DataAccessException("Username may not be empty or null.");
        }

        if (!userExists(username)) {
            throw new DataAccessException("Error: User does not exist.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            String statement = "INSERT INTO auth (authToken, username) VALUES (?,?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, newAuthToken);
                ps.setString(2, username);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Error: Failed to create or update auth token for user: " + username);
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating authToken.", e);
        }
        return newAuthToken;
    }

    private String createAuthToken() {
        return UUID.randomUUID().toString();
    }

    private boolean userExists(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT COUNT(*) FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking user existence.", e);
        }
        return false;
    }

    private String getAuthTokenByUsername(String username, Connection conn) throws SQLException {
        String statement = "SELECT authToken FROM auth WHERE username=?";
        try(var ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try(var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("authToken");
                }
            }
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
       try (var conn = DatabaseManager.getConnection()) {
           var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
           try(var ps = conn.prepareStatement(statement)) {
               ps.setString(1, authToken);
               try (var rs = ps.executeQuery()) {
                   if (rs.next()) {
                       return readAuth(rs);
                   }
               }
           }
       } catch (SQLException e) {
           throw new DataAccessException("Failed to retrieve AuthData.", e);
       }
       return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String authToken = rs.getString("authToken");

        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("No auth token found for deletion: " + authToken);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete AuthData.", e);
        }
    }

    @Override
    public void clearAllAuthTokens() throws DataAccessException {
        String statement = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
        var ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth", e);
        }
    }
}
