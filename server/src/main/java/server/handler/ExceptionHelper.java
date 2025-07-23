package server.handler;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.InvalidPasswordException;
import dataaccess.exceptions.UnauthorizedException;
import spark.Response;

import java.util.Map;

public class ExceptionHelper {
    private static final Gson gson = new Gson();

    public static String handleException(Exception e, Response response) {
        response.type("application/json");

        if (e instanceof BadRequestException) {
            response.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        } else if (e instanceof UnauthorizedException || e instanceof InvalidPasswordException) {
            response.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } else if (e instanceof AlreadyTakenException) {
            response.status(403);
            return gson.toJson(Map.of("message", "Error: already taken."));
        } else {
            response.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
