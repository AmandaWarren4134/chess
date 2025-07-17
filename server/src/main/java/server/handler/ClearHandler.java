package server.handler;

import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public ClearHandler(UserService userService, GameService gameService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            userService.clearUserData();
            authService.clearAuthData();
            gameService.clearGameData();

            response.status(200);
            return "Database successfully cleared.";
        } catch (Exception e) {
            response.status(500);
            return "Error clearing database: " + e.getMessage();
        }
    }
}
