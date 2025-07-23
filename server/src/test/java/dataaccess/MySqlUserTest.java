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
    @DisplayName("Positive CreateUser")
    void createUser() throws Exception {
        String username = "firstUser";
        String password = "MyPassword123!!!";
        String email = "firstUser@gmail.com";

        // Create a user
        userDao.createUser(username, password, email);

        // Retrieve user
        UserData userData = userDao.getUser(username);

        assertNotNull(userData);
        assertEquals(username, userData.username());
        assertEquals(email, userData.email());
        assertNotEquals(password, userData.password());

        assertTrue(checkpw(password, userData.password()));
    }

    @Test
    void getUser() {
    }

    @Test
    void clearAllUsers() {
    }
}