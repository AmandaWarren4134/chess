package server.handler;

import com.google.gson.Gson;
import dataaccess.exceptions.*;
import spark.Response;

import java.sql.SQLException;

import java.sql.SQLNonTransientConnectionException;
import java.util.Map;

public class ExceptionHelper {
    private static final Gson GSON = new Gson();

    public static String handleException(Exception e, Response response) {
        response.type("application/json");

        if (e instanceof BadRequestException) {
            response.status(400);
            return GSON.toJson(Map.of("message", "Error: bad request"));
        } else if (e instanceof UnauthorizedException || e instanceof InvalidPasswordException) {
            response.status(401);
            return GSON.toJson(Map.of("message", "Error: unauthorized"));
        } else if (e instanceof AlreadyTakenException) {
            response.status(403);
            return GSON.toJson(Map.of("message", "Error: already taken."));
        } else if (e instanceof DataAccessException || e instanceof SQLException) {
            response.status(500);
            return GSON.toJson(Map.of("message", "Error: database connection failed"));
        }
        else {
            response.status(500);
            return GSON.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
