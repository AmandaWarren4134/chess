package server.handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.InvalidPasswordException;
import dataaccess.UnauthorizedException;
import service.UserService;
import service.request.LogoutRequest;
import service.response.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;


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
        } catch (BadRequestException e) {
            response.status(400);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedException | InvalidPasswordException e ) {
            response.status(401);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
