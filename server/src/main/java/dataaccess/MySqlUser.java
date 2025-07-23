package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUser implements IUserDAO {

    public MySqlUser() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public void createUser(String username, String password, String email) throws AlreadyTakenException {
        String hashedPassword = hashPassword(password);

        try (Connection conn = DatabaseManager.getConnection()) {

            // Check if username is taken
            String checkForUsername = "SELECT username FROM user WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkForUsername)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        throw new AlreadyTakenException("Username: " + username + " already taken.");
                    }
                }
            }

            // Insert User
            String insertUserSQL = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertUserSQL)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, email);
                stmt.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new AlreadyTakenException("Failed to create user", e);
        }
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException ("Failed to retrieve user", e);
        }
        return null; // user not found
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");

        return new UserData(username, password, email);
    }

    @Override
    public void clearAllUsers() {
        String statement = "DELETE FROM user";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
        }
    }

}
