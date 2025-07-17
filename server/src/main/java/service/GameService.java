package service;

import Model.GameData;
import dataaccess.*;
import service.request.CreateRequest;
import service.request.ListRequest;
import service.response.CreateResult;
import service.response.ListResult;

import java.util.ArrayList;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService (GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        // Validate Request
        validateCreateRequest(createRequest);

        // Check AuthData
        if (authDAO.getAuth(createRequest.authToken()) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Create Game
        int newGameID = gameDAO.createGame(createRequest.gameName());

        // Return as a CreateResult
        return new CreateResult(newGameID);
    }

    private void validateCreateRequest(CreateRequest request) throws DataAccessException {
        if (request.gameName() == null || request.gameName().isBlank() || request.authToken() == null || request.authToken().isBlank()){
            throw new BadRequestException("Error: bad request.");
        }
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        // Validate Request
        validateListRequest(listRequest);

        // Check AuthData
        if (authDAO.getAuth(listRequest.authToken()) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Get List of Games
        ArrayList<GameData> games = gameDAO.listGames();

        // Return as a ListResult
        return new ListResult(games);
    }

    private void validateListRequest(ListRequest request) throws DataAccessException {
        if (request.authToken() == null || request.authToken().isBlank()){
            throw new UnauthorizedException("Error: unauthorized.");
        }
    }

    public void clearGameData () {
        gameDAO.clearAllGames();
    }
}
