package server.handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import service.request.JoinRequest;
import service.response.JoinResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinHandler implements Route {
    private final GameService gameService;
    private final Gson gson;

    public JoinHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            JoinRequest bodyRequest = gson.fromJson(request.body(), JoinRequest.class);
            JoinRequest joinRequest = new JoinRequest(authToken, bodyRequest.playerColor(), bodyRequest.gameID());
            JoinResult result = gameService.join(joinRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (BadRequestException e) {
            response.status(400);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedException e) {
            response.status(401);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            response.status(403);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: already taken"));
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");

            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
