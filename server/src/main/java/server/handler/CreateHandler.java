package server.handler;

import com.google.gson.Gson;
import service.GameService;
import service.request.CreateRequest;
import service.response.CreateResult;
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
            CreateRequest createRequest = new CreateRequest(bodyRequest.gameName(), authToken);
            CreateResult result = gameService.create(createRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
