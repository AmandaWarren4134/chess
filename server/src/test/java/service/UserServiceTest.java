package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.MySqlAuth;
import dataaccess.MySqlGame;
import dataaccess.MySqlUser;
import org.junit.jupiter.api.*;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.response.LoginResult;
import service.response.LogoutResult;
import service.response.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService testUserService;
    private MySqlUser testUserDAO;
    private MySqlAuth testAuthDAO;

    @BeforeEach
    public void setUp() {
        testUserDAO = new MySqlUser();
        testAuthDAO = new MySqlAuth();
        testUserService = new UserService(testUserDAO, testAuthDAO);

    }

    @Test
    @DisplayName("Positive Register")
    public void normalRegister() {
        RegisterRequest testRequest = new RegisterRequest("Amanda", "abcdefg", "amanda@gmail.com");
        try {
            RegisterResult result = testUserService.register(testRequest);
            assertNotNull(result);
            assertEquals("Amanda", result.username());
            assertNotNull(result.authToken());
        } catch (DataAccessException ex) {
            System.err.print("Database error during registration: " + ex.getMessage());
        }
    }

    @Test
    @DisplayName("Negative Register")
    public void badRequestRegister() {
        RegisterRequest testRequest = new RegisterRequest("Amanda", null, "amanda@gmail.com");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            testUserService.register(testRequest);
        });
    }

    @Test
    @DisplayName("Positive Login")
    public void normalLogin() {
        RegisterRequest registerRequest = new RegisterRequest("perfectUser", "perfect", "perfect@gmail.com");
        try {
            testUserService.register(registerRequest);
        } catch (DataAccessException ex) {
            System.err.print("Database error during registration: " + ex.getMessage());
        }
        LoginRequest loginRequest = new LoginRequest("perfectUser", "perfect");

        try {
            LoginResult result = testUserService.login(loginRequest);
            assertNotNull(result);
            assertEquals("perfectUser", result.username());
            assertNotNull(result.authToken());
        } catch (DataAccessException ex) {
            System.err.print("Database error during login: " + ex.getMessage());
        }
    }

    @Test
    @DisplayName("Negative Login")
    public void wrongPasswordLogin() {
        RegisterRequest registerRequest = new RegisterRequest("imperfectUser", "perfect", "perfect@gmail.com");
        try {
            testUserService.register(registerRequest);
        } catch (DataAccessException ex) {
            System.err.print("Database error during registration: " + ex.getMessage());
        }

        LoginRequest loginRequest = new LoginRequest("imperfectUser", "imperfect");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            testUserService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("Positive Logout")
    public void normalLogout() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("perfectUser", "perfect", "perfect@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        String authToken = registerResult.authToken();

        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        LogoutResult result = testUserService.logout(logoutRequest);
        assertNotNull(result);
        assertThrows(DataAccessException.class, () -> {
            testAuthDAO.getAuth(authToken);
        });

    }

    @Test
    @DisplayName("Negative Logout")
    public void wrongUserLogout() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("perfectUser", "perfect", "perfect@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        String authToken = registerResult.authToken();

        LogoutRequest logoutRequest = new LogoutRequest("fake");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            testUserService.logout(logoutRequest);
        });
    }
}
