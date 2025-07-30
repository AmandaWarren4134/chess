package server.handler;

import com.google.gson.Gson;
import service.GameService;
import request.CreateRequest;
import response.CreateResult;
import spark.Request;
import spark.Response;
import spark.Route;

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

            System.out.println("Header Auth: " + authToken);
            System.out.println("Raw body: " + request.body());
            System.out.println("Parsed game name: " + bodyRequest.gameName());

            CreateRequest createRequest = new CreateRequest(bodyRequest.gameName(), authToken);
            CreateResult result = gameService.create(createRequest);

            response.status(200);
            response.type("application/json");
            System.out.println("Returning CreateResult: " + gson.toJson(result));   // debug
            return gson.toJson(result);
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
