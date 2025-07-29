package service;

import dataaccess.exceptions.DataAccessException;
import dataaccess.MySqlAuth;
import dataaccess.MySqlGame;
import dataaccess.MySqlUser;
import org.junit.jupiter.api.Test;
import server.handler.ClearHandler;
import request.CreateRequest;
import request.ListRequest;
import request.RegisterRequest;
import response.ListResult;
import response.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class ClearHandlerTest {

    private UserService testUserService;
    private AuthService testAuthService;
    private GameService testGameService;
    private MySqlUser testUserDAO;
    private MySqlAuth testAuthDAO;
    private MySqlGame testGameDAO;

    @Test
    void handle() throws DataAccessException {
        // Set up
        testUserDAO = new MySqlUser();
        testAuthDAO = new MySqlAuth();
        testGameDAO = new MySqlGame();
        testUserService = new UserService(testUserDAO, testAuthDAO);
        testAuthService = new AuthService(testAuthDAO);
        testGameService = new GameService(testGameDAO, testAuthDAO);

        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);

        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("firstGame", authToken);
        testGameService.create(createRequest);

        // Assert that they were created
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listOfGames = testGameService.list(listRequest);
        assertEquals(listOfGames.games().size(), 1);

        assertNotNull(testAuthDAO.getAuth(authToken));

        // Run the handle
        ClearHandler clearHandler = new ClearHandler(testUserService, testGameService, testAuthService);
        Object result = clearHandler.clear();

        assertEquals("{}", result);

        // Assertions
        assertNull(testAuthDAO.getAuth(authToken));

        // Re-register to test games list
        RegisterResult newRegister = testUserService.register(registerRequest);
        String newAuthToken = newRegister.authToken();

        // Verify empty games list
        ListResult freshList = testGameService.list(new ListRequest(newAuthToken));
        assertEquals(0, freshList.games().size());
    }
}