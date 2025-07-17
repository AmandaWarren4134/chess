package service;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import passoff.model.TestAuthResult;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.response.LoginResult;
import service.response.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService testUserService;

    @BeforeEach
    public void setUp() {
        testUserService = new UserService();

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
}
