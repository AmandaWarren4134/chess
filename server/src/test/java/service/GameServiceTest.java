package service;

import com.google.gson.Gson;
import chess.ChessGame;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.*;
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
    private static MySqlUser testUserDAO;
    private static MySqlGame testGameDAO;
    private static MySqlAuth testAuthDAO;
    private final Gson gson = new Gson();

    @BeforeAll
    static void setup() {
        testUserDAO = new MySqlUser();
        testGameDAO = new MySqlGame();
        testAuthDAO = new MySqlAuth();
    }

    @BeforeEach
    public void clearDatabaseBeforeEach() {
        // Initialize the services with the DAOs
        testUserService = new UserService(testUserDAO, testAuthDAO);
        testGameService = new GameService(testGameDAO, testAuthDAO);

        // Clear the database before each test (ensure methods are present in your DAOs)
        try {
            testGameDAO.clearAllGames();
            testUserDAO.clearAllUsers();
            testAuthDAO.clearAllAuthTokens();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void clearDatabaseAfterEach() throws DataAccessException {
        testGameDAO.clearAllGames();
        testUserDAO.clearAllUsers();
        testAuthDAO.clearAllAuthTokens();
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