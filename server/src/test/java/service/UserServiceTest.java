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
        LoginRequest testRequest = new LoginRequest("Amanda", "abcdefg");
        try {
            RegisterResult result = testUserService.register(testRequest);
            assertNotNull(result);
            assertEquals("Amanda", result.username());
            assertNotNull(result.authToken());
        } catch (DataAccessException ex) {
            System.err.print("Database error during registration: " + ex.getMessage());
        }
    }
}
