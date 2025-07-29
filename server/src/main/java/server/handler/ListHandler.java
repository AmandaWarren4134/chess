package server.handler;

import com.google.gson.Gson;
import service.GameService;
import request.ListRequest;
import response.ListResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListHandler implements Route {

    private final GameService gameService;
    private final Gson gson;

    public ListHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            ListRequest listRequest = new ListRequest(authToken);
            ListResult result = gameService.list(listRequest);

            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
