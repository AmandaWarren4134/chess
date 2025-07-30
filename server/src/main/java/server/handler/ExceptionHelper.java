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

        int status;
        String message;

        if (e instanceof BadRequestException) {
            status = 400;
            message = "Error: bad request";
        } else if (e instanceof UnauthorizedException || e instanceof InvalidPasswordException) {
            status = 401;
            message = "Error: unauthorized";
        } else if (e instanceof AlreadyTakenException) {
            status = 403;
            message = "Error: already taken";
        } else if (e instanceof DataAccessException || e instanceof SQLException) {
            status = 500;
            message = "Error: database connection failed";
        }
        else {
            status = 500;
            message = "Error: " + e.getMessage();
        }

        response.status(status);
        return GSON.toJson(Map.of("message", message, "status", status));
    }
}
