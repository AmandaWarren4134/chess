package dataaccess;

import dataaccess.MySqlUser;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mindrot.jbcrypt.BCrypt.checkpw;

class MySqlUserTest {

    private static MySqlUser userDao;

    @BeforeAll
    static void setup() throws DataAccessException {
        userDao = new MySqlUser();
    }

    @BeforeEach
    void clearDatabaseBeforeEach() throws DataAccessException {
        userDao.clearAllUsers();
    }

    @AfterEach
    void clearDatabaseAfterEach() throws DataAccessException {
        userDao.clearAllUsers();
    }

    @Test
    @DisplayName("Positive createUser")
    void createUser() throws Exception {
        String username = "firstUser";
        String password = "MyPassword123!!!";
        String email = "firstUser@gmail.com";

        // Create a user
        userDao.createUser(username, password, email);

        // Retrieve user
        UserData userData = userDao.getUser(username);

        // Assertions
        assertNotNull(userData);
        assertEquals(username, userData.username());
        assertEquals(email, userData.email());
        assertNotEquals(password, userData.password());

        assertTrue(checkpw(password, userData.password()));
    }

    @Test
    @DisplayName ("Username already taken")
    void createInvalidUser() throws Exception {
        String username = "firstUser";
        String password = "MyPassword123!!!";
        String email = "firstUser@gmail.com";

        String otherUsername = "firstUser";
        String otherPassword = "newpassword";
        String otherEmail = "newemail@gmail.com";

        // Create first user
        userDao.createUser(username, password, email);

        // Attempt to create second user with same username
        AlreadyTakenException ex = assertThrows(AlreadyTakenException.class, () -> {
            userDao.createUser(otherUsername, otherPassword, otherEmail);
        });
        System.out.print("Exception message: " + ex.getMessage());

        // Assertions
        assertTrue(ex.getCause().getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Positive getUser")
    void getUser() throws Exception {
        String username = "myUsername";
        String password = "password123";
        String email = "email@gmail.com";

        // Create a user
        userDao.createUser(username, password, email);

        // Get the user
        UserData userData = userDao.getUser("myUsername");

        // Assertions
        assertNotNull(userData);
        assertEquals(username, userData.username());
        assertEquals(email, userData.email());
        assertNotEquals(password, userData.password());

        assertTrue(checkpw(password, userData.password()));
    }

    @Test
    @DisplayName("Get Username not in Database")
    void getInvalidUserReturnsNull() throws Exception {
        // Try to get user from empty database
        UserData userData = userDao.getUser("myUsername");

        // Assertions
        assertNull(userData);
    }

    @Test
    void clearAllUsers() throws Exception {
        // Put users in the database
        userDao.createUser("Bella", "forks", "bellaswan@gmail.com");
        userDao.createUser("Edward", "vampire", "edwardcullen@gmail.com");

        // Make sure they exist
        assertNotNull(userDao.getUser("Bella"));
        assertNotNull(userDao.getUser("Edward"));

        // Clear users
        userDao.clearAllUsers();

        // Check database
        assertNull(userDao.getUser("Bella"));
        assertNull(userDao.getUser("Edward"));
    }
}