package server.handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.InvalidPasswordException;
import dataaccess.UnauthorizedException;
import service.GameService;
import service.request.CreateRequest;
import service.request.LogoutRequest;
import service.response.CreateResult;
import service.response.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateHandler implements Route {
    private final GameService gameService;
    private final Gson gson;

    public CreateHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            CreateRequest bodyRequest = gson.fromJson(request.body(), CreateRequest.class);
            CreateRequest createRequest = new CreateRequest(bodyRequest.gameName(), authToken);
            CreateResult result = gameService.create(createRequest);

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
