package service;

import Model.GameData;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import service.request.ListRequest;
import service.response.ListResult;

import java.util.ArrayList;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService (GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        // Validate Request
        validateListRequest(listRequest);

        // Get AuthData
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
