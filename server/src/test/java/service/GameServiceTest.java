package service;

import com.google.gson.Gson;
import chess.ChessGame;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.request.CreateRequest;
import service.request.JoinRequest;
import service.request.ListRequest;
import service.request.RegisterRequest;
import service.response.CreateResult;
import service.response.JoinResult;
import service.response.ListResult;
import service.response.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private UserService testUserService;
    private GameService testGameService;
    private UserDAO testUserDAO;
    private GameDAO testGameDAO;
    private AuthDAO testAuthDAO;
    private final Gson gson = new Gson();


    @BeforeEach
    public void setUp() {
        testUserDAO = new UserDAO();
        testGameDAO = new GameDAO();
        testAuthDAO = new AuthDAO();
        testUserService = new UserService(testUserDAO, testAuthDAO);
        testGameService = new GameService(testGameDAO, testAuthDAO);
    }

    @Test
    @DisplayName("Positive Join")
    void normalJoin() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", authToken);
        CreateResult createResult = testGameService.create(createRequest);
        // Create Join Request
        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, createResult.gameID());
        JoinResult joinResult = testGameService.join(joinRequest);
        Object result = gson.toJson(joinResult);
        // Assertions
        assertEquals("{}", result);
        assertEquals("newUser", testGameDAO.getGame(createResult.gameID()).whiteUsername());
    }

    @Test
    @DisplayName("Negative Join")
    void colorTakenJoin() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", authToken);
        CreateResult createResult = testGameService.create(createRequest);
        // Create Join Request
        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, createResult.gameID());
        JoinResult joinResult = testGameService.join(joinRequest);
        Object result = gson.toJson(joinResult);
        // Create Another User
        RegisterRequest otherRegisterRequest = new RegisterRequest("otherUser", "abcd123", "user2@gmail.com");
        RegisterResult otherRegisterResult = testUserService.register(otherRegisterRequest);
        // Try to join the same color
        JoinRequest badJoinRequest = new JoinRequest(otherRegisterResult.authToken(), ChessGame.TeamColor.WHITE, createResult.gameID());

        // Assertions
        assertThrows(AlreadyTakenException.class, () -> {
            testGameService.join(badJoinRequest);
        });
    }

    @Test
    @DisplayName("Positive Create")
    void normalCreate() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", authToken);
        CreateResult createResult = testGameService.create(createRequest);
        // Assertions
        assertDoesNotThrow(() -> {
            testGameDAO.getGame(createResult.gameID());
        });
    }

    @Test
    @DisplayName("Negative Create")
    void badRequestCreate() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", "wrongAuthToken");
        // Assertions
        assertThrows(DataAccessException.class, () -> {
            testGameService.create(createRequest);
        });
    }

    @Test
    @DisplayName("Positive List")
    void normalList() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", authToken);
        CreateResult createResult = testGameService.create(createRequest);
        // Get a list of games
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = testGameService.list(listRequest);
        // Assertions
        assertNotNull(listResult);
    }

    @Test
    @DisplayName("Negative List")
    void noAuthList() throws DataAccessException {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("newUser", "abc123", "user@gmail.com");
        RegisterResult registerResult = testUserService.register(registerRequest);
        // Create a game
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("aGame", authToken);
        CreateResult createResult = testGameService.create(createRequest);
        // Get a list of games
        ListRequest listRequest = new ListRequest("badAuthToken");
        // Assertions
        assertThrows(DataAccessException.class, () -> {
            testGameService.list(listRequest);
        });
    }
}