package server.handler;

import com.google.gson.Gson;
import service.GameService;
import request.JoinRequest;
import response.JoinResult;
import spark.Request;
import spark.Response;
import spark.Route;

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
        } catch (Exception e) {
            return ExceptionHelper.handleException(e, response);
        }
    }
}
