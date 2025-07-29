package server.handler;

import com.google.gson.Gson;
import service.UserService;
import request.LogoutRequest;
import response.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;


public class LogoutHandler implements Route {
    private final UserService userService;
    private final Gson gson;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult result = userService.logout(logoutRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
