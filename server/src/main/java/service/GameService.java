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

    public GameService () {
        this.gameDAO = new GameDAO();
        this.authDAO = new AuthDAO();
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        // Validate Request
        validateListRequest(listRequest);

        // Get AuthData
        try {
            authDAO.getAuth(listRequest.authToken());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        // Get List of Games
        ArrayList<GameData> gameList = gameDAO.listGames();

        // Return as a ListResult
        return new ListResult(gameList);
    }

    private void validateListRequest(ListRequest request) throws DataAccessException {
        if (request == null || request.authToken() == null || request.authToken().isBlank()){
            throw new UnauthorizedException("Error: unauthorized.");
        }
    }

    public void clearGameData () {
        gameDAO.clearAllGames();
    }
}
