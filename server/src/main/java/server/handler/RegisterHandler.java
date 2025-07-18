package server.handler;

import com.google.gson.Gson;
import service.UserService;
import service.request.RegisterRequest;
import service.response.RegisterResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;


public class RegisterHandler implements Route {

    private final UserService userService;
    private final Gson gson;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);

        try {
            RegisterResult result = userService.register(registerRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(Map.of("username", result.username(), "authToken", result.authToken()));
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
