package server.handler;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
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
    private final Gson gson;

    public ClearHandler(UserService userService, GameService gameService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
        this.gson = new Gson();
    }

    public String clear() throws DataAccessException {
        userService.clearUserData();
        authService.clearAuthData();
        gameService.clearGameData();
        return "{}";
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            response.status(200);
            return clear();
        } catch (Exception e) {
            response.status(500);
            return "Error clearing database: " + e.getMessage();
        }
    }
}
