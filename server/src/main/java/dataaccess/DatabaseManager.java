package dataaccess;

import dataaccess.exceptions.DataAccessException;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            System.err.println("Database connection failed: " + ex.getMessage());
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    private static final String[] CREATE_STATEMENTS = {
            // user table
            """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(100) PRIMARY KEY,
            password VARCHAR(100) NOT NULL,
            email VARCHAR(100) NOT NULL
        )
        """,
            // auth table
            """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(100),
            username VARCHAR(100) NOT NULL,
            FOREIGN KEY (username) REFERENCES user(username)
        )
        """,
            // game table
            """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT PRIMARY KEY AUTO_INCREMENT,
            whiteUsername VARCHAR(100),
            blackUsername VARCHAR(100),
            gameName VARCHAR(100) NOT NULL,
            gameState JSON,
            FOREIGN KEY (whiteUsername) REFERENCES user(username),
            FOREIGN KEY (blackUsername) REFERENCES user(username)
        )
        """
    };

    public static void configureDatabase() throws DataAccessException {
        createDatabase();
        try (Connection conn = getConnection()) {
            for (String stmt : CREATE_STATEMENTS) {
                try (PreparedStatement ps = conn.prepareStatement(stmt)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database", ex);
        }
    }
}
