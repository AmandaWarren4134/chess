package server;

import server.handler.RegisterHandler;
import server.handler.ClearHandler;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        UserService userService = new UserService();
        AuthService authService = new AuthService();
        GameService gameService = new GameService();

        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService, gameService, authService);

        Spark.post("/user", registerHandler);
        Spark.delete("/db", clearHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
