package server;

import dataaccess.UserDAO;
import dataaccess.MySqlAuth;
import dataaccess.MySqlGame;
import dataaccess.MySqlUser;
import server.handler.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Shared DAOs
        MySqlAuth authDAO = new MySqlAuth();
        MySqlGame gameDAO = new MySqlGame();
        MySqlUser userDAO = new MySqlUser();

        // Shared Services
        UserService userService = new UserService(userDAO, authDAO);
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);

        // Create Endpoint Handlers
        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService, gameService, authService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListHandler listHandler = new ListHandler(gameService);
        CreateHandler createHandler = new CreateHandler(gameService);
        JoinHandler joinHandler = new JoinHandler(gameService);

        // Registering Endpoints to receive HTTP requests
        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.get("/game", listHandler);
        Spark.post("/game", createHandler);
        Spark.put("/game", joinHandler);
        Spark.delete("/db", clearHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
