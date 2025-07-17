package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
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
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();

        // Shared Services
        UserService userService = new UserService(userDAO, authDAO);
        AuthService authService = new AuthService();
        GameService gameService = new GameService(gameDAO, authDAO);

        // Register your endpoints and handle exceptions here.
        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService, gameService, authService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListHandler listHandler = new ListHandler(gameService);
        //CreateHandler createHandler = new CreateHandler();
        //JoinHandler joinHandler = new JoinHandler();

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.get("/game", listHandler);
//        Spark.post("/game", createHandler);
//        Spark.put("/game", joinHandler);
        Spark.delete("/db", clearHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
