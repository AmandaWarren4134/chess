package server.handler;

import com.google.gson.Gson;
import service.UserService;
import service.request.LoginRequest;
import service.response.LoginResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LoginHandler implements Route {

    private final UserService userService;
    private final Gson gson;

    public LoginHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

        try {
            LoginResult result = userService.login(loginRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(Map.of("username", result.username(), "authToken", result.authToken()));
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
