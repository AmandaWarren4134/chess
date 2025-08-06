package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import request.*;
import response.*;
import exception.*;
import server.Server;
import server.ServerFacade;
import websocket.ServerMessageObserver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String existingAuthToken;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabaseBeforeEach() throws Exception {
        clearDatabase();
        var result = facade.register(new RegisterRequest("amanda", "password", "amanda@gmail.com"));
        this.existingAuthToken = result.authToken();
        facade.setAuthToken(existingAuthToken);
    }

    @Test
    @DisplayName("Positive Register")
    void normalRegister() throws Exception {
        var request = new RegisterRequest("myUser", "myPass", "myUser@gmail.com");
        var result = facade.register(request);
        assertNotNull(result.authToken());
        assertEquals("myUser", result.username());
    }

    @Test
    @DisplayName("Positive Login")
    void normalLogin() throws Exception {
        var request = new LoginRequest("amanda", "password");
        var result = facade.login(request);
        assertNotNull(result.authToken());
        assertEquals("amanda", result.username());
    }

    @Test
    @DisplayName("Positive Logout")
    void normalLogout() throws Exception {
        facade.logout();

        var ex = assertThrows(ResponseException.class, () -> {
            ListRequest listRequest = new ListRequest(existingAuthToken);
            facade.list(listRequest);
        });

        assertEquals(401, ex.statusCode());
    }

    @Test
    @DisplayName("Positive List")
    void normalList() throws Exception {
        facade.create(new CreateRequest("game1", existingAuthToken));
        facade.create(new CreateRequest("game2", existingAuthToken));

        var request = new ListRequest(existingAuthToken);
        var result = facade.list(request);

        assertEquals(2, result.games().size());
        assertEquals("game1", result.games().getFirst().gameName());
    }

    @Test
    @DisplayName("Positive Create")
    void normalCreate() throws Exception {
        var request = new CreateRequest("game1", existingAuthToken);
        var result = facade.create(request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Positive Join")
    void normalJoin() throws Exception {
        var result = facade.create(new CreateRequest("game2", existingAuthToken));
        var request = new JoinRequest(existingAuthToken,  ChessGame.TeamColor.BLACK, result.gameID());

        facade.join(request);

        var listRequest = new ListRequest(existingAuthToken);
        var listResult = facade.list(listRequest);

        assertEquals("amanda", listResult.games().getFirst().blackUsername());

    }

    @Test
    @DisplayName("Register Username Taken")
    void usernameTakenRegister() throws Exception {
        var request = new RegisterRequest("amanda", "newPassword", "amanda2@gmail.com");
        var ex = assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });

        assertEquals(403, ex.statusCode());
    }

    @Test
    @DisplayName("Login With Nonexisting User")
    void invalidUserLogin() throws Exception {
        var request = new LoginRequest("userDoesNotExist", "password");

        var ex = assertThrows(ResponseException.class, () -> {
            facade.login(request);
        });

        assertEquals(401, ex.statusCode());
    }

    @Test
    @DisplayName("Logout Twice")
    void doubleLogout() throws Exception {
        facade.logout();

        var ex = assertThrows(ResponseException.class, () -> {
            facade.logout();
        });
        assertEquals(401, ex.statusCode());
    }

    @Test
    @DisplayName("List No Games")
    void noGamesList() throws Exception {
        var request = new ListRequest(existingAuthToken);
        var result = facade.list(request);

        assertEquals(0, result.games().size());
    }

    @Test
    @DisplayName("Invalid AuthToken")
    void invalidAuthTokenList() throws Exception {
        facade.setAuthToken("invalidAuthToken");
        var request = new ListRequest("invalidAuthToken");

        var ex = assertThrows(ResponseException.class, () -> {
            facade.list(request);
        });

        assertEquals(401, ex.statusCode());
    }

    @Test
    @DisplayName("Create Game With Existing Name")
    void create() throws Exception {
        facade.create(new CreateRequest("game1", existingAuthToken));
        var ex = assertThrows(ResponseException.class, () -> {
            facade.create(new CreateRequest("game1", existingAuthToken));
        });

        assertEquals(403, ex.statusCode());
    }

    @Test
    @DisplayName("Join An Occupied Team")
    void join() throws Exception {
        var result = facade.create(new CreateRequest("game2", existingAuthToken));
        var request = new JoinRequest(existingAuthToken,  ChessGame.TeamColor.BLACK, result.gameID());

        facade.join(request);

        // Create a second user and request to join the same team color
        var secondRegister = facade.register(new RegisterRequest("secondUser", "password", "secondUser@gmail.com"));
        facade.setAuthToken(secondRegister.authToken());

        var ex = assertThrows(ResponseException.class, () -> {
            JoinRequest secondJoin = new JoinRequest(existingAuthToken, ChessGame.TeamColor.BLACK, result.gameID());
            facade.join(secondJoin);
        }) ;

        assertEquals(403, ex.statusCode());
    }

    private void clearDatabase() throws Exception {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(facade.getServerUrl() + "/db"))
                .DELETE()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to clear DB: " + response.body());
        }
    }
}
