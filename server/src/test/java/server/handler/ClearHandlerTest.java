package server.handler;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.GameService;
import service.UserService;
import service.request.CreateRequest;
import service.request.ListRequest;
import service.request.RegisterRequest;
import service.response.ListResult;
import service.response.RegisterResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClearHandlerTest {

    private UserService testUserService;
    private AuthService testAuthService;
    private GameService testGameService;
    private UserDAO testUserDAO;
    private AuthDAO testAuthDAO;
    private GameDAO testGameDAO;

    @Test
    void handle() throws DataAccessException {
        // Set up
        testUserDAO = new UserDAO();
        testAuthDAO = new AuthDAO();
        testGameDAO = new GameDAO();
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
        assertThrows(DataAccessException.class, () -> {
            testAuthDAO.getAuth(authToken);
        });

        // Re-register to test games list
        RegisterResult newRegister = testUserService.register(registerRequest);
        String newAuthToken = newRegister.authToken();

        // Verify empty games list
        ListResult freshList = testGameService.list(new ListRequest(newAuthToken));
        assertEquals(0, freshList.games().size());
    }
}