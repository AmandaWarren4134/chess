package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import request.*;
import response.*;
import exception.*;
import server.Server;
import server.ServerFacade;

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
    public static void init() {
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
    void register() throws Exception {
        var request = new RegisterRequest("amanda", "newPassword", "amanda2@gmail.com");
        var ex = assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });

        assertEquals(403, ex.statusCode());
    }

    @Test
    void login() throws Exception {
        var request = new LoginRequest("userDoesNotExist", "password");

        var ex = assertThrows(ResponseException.class, () -> {
            facade.login(request);
        });

        assertEquals(401, ex.statusCode());
    }

    @Test
    void logout() {
    }

    @Test
    void list() {
    }

    @Test
    void create() {
    }

    @Test
    void join() {
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
