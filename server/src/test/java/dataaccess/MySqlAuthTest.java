package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import model.AuthData;
import dataaccess.MySqlAuth;
import dataaccess.MySqlGame;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

class MySqlAuthTest {

    private static MySqlAuth authDao;
    private static MySqlUser userDao;
    private static MySqlGame gameDao;

    @BeforeAll
    static void setup() throws DataAccessException {
        authDao = new MySqlAuth();
        userDao = new MySqlUser();
        gameDao = new MySqlGame();
    }

    @BeforeEach
    void ClearDatabaseBeforeEach() throws DataAccessException {
        authDao.clearAllAuthTokens();

        gameDao.clearAllGames();

        userDao.clearAllUsers();

        userDao.createUser("firstUser", "MyPassword123!!!", "firstUser@gmail.com");
    }

    @AfterEach
    void clearDatabaseAfterEach() throws DataAccessException {
        authDao.clearAllAuthTokens();
        userDao.clearAllUsers();
    }

    @Test
    @DisplayName("Positive createAuth")
    void createAuth() throws DataAccessException {
        // Valid username
        String authToken = authDao.createAuth("firstUser");

        // Assertions
        assertNotNull(authToken);
    }

    @Test
    @DisplayName("Negative createAuth")
    void createAuthWithInvalidUsername() throws DataAccessException {
        // Invalid username
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authDao.createAuth("");
        });
        System.out.print("Exception message: " + ex.getMessage());
    }

    @Test
    @DisplayName("Positive getAuth")
    void getAuth() throws DataAccessException{
        // Create authToken
        String authToken = authDao.createAuth("firstUser");

        // Get authToken
        AuthData authData = authDao.getAuth(authToken);

        // Assertions
        assertNotNull(authData);
        assertEquals(authToken, authData.authToken());
        assertEquals("firstUser", authData.username());
    }

    @Test
    @DisplayName("Negative getAuth")
    void getAuthWrongToken() throws DataAccessException {
        // Invalid authToken
        AuthData authData = authDao.getAuth("notARealAuthToken");
        assertNull(authData);
    }

    @Test
    @DisplayName("Positive deleteAuth")
    void deleteAuth() throws DataAccessException {
        // Create authToken
        String authToken = authDao.createAuth("firstUser");

        // Delete authToken
        authDao.deleteAuth(authToken);

        // Get authToken
        AuthData authData = authDao.getAuth(authToken);

        // Assertions
        assertNull(authData);
    }

    @Test
    @DisplayName("Negative deleteAuth, authToken does not exist")
    void deleteInvalidAuth() {
        // Invalid authToken
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authDao.deleteAuth("thisIsNotARealAuthToken");
        });
        System.out.print("Exception message: " + ex.getMessage());
    }

    @Test
    @DisplayName("Clear all auth tokens")
    void clearAllAuthTokens() throws DataAccessException {
        // Fill the database
        String firstToken = authDao.createAuth("firstUser");
        String secondToken = authDao.createAuth("second");
        String thirdToken = authDao.createAuth("third");

        // Make sure they exist
        assertNotNull(authDao.getAuth(firstToken));
        assertNotNull(authDao.getAuth(secondToken));
        assertNotNull(authDao.getAuth(thirdToken));

        // Clear
        authDao.clearAllAuthTokens();

        // Check database
        assertNull(authDao.getAuth(firstToken));
        assertNull(authDao.getAuth(secondToken));
        assertNull(authDao.getAuth(thirdToken));
    }
}