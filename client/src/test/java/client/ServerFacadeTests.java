package client;

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
        var request = new LogoutRequest(existingAuthToken);
        facade.logout(request);

        var ex = assertThrows(ResponseException.class, () -> {
            ListRequest listRequest = new ListRequest(existingAuthToken);
            facade.list(listRequest);
        });

        assertEquals(401, ex.statusCode());
    }

    @Test
    @DisplayName("Positive List")
    void normalList() {
    }

    @Test
    @DisplayName("Positive Create")
    void normalCreate() {
    }

    @Test
    @DisplayName("Positive Join")
    void normalJoin() {
    }

    @Test
    void register() {
    }

    @Test
    void login() {
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
