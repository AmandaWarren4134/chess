package service;

import model.AuthData;
import model.GameData;
import chess.ChessGame;
import dataaccess.*;
import service.request.CreateRequest;
import service.request.JoinRequest;
import service.request.ListRequest;
import service.response.CreateResult;
import service.response.JoinResult;
import service.response.ListResult;

import java.util.ArrayList;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService (GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        // Validate Request
        validateJoinRequest(joinRequest);

        // Check AuthData
        if (authDAO.getAuth(joinRequest.authToken()) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        // Get GameData
        GameData game = gameDAO.getGame(joinRequest.gameID());

        // Check Color Availability
        if (!colorIsAvailable(game, joinRequest.playerColor())) {
            throw new AlreadyTakenException("Error: color already taken");
        }

        // Get Username
        AuthData authData = authDAO.getAuth(joinRequest.authToken());
        String user = authData.username();

        // Update Game
        GameData updatedGame = game.update(user, joinRequest.playerColor(), game.game());
        gameDAO.updateGame(joinRequest.gameID(), updatedGame);

        return new JoinResult();
    }

    private boolean colorIsAvailable (GameData game, ChessGame.TeamColor color) {
        if (game.whiteUsername() == null && color == ChessGame.TeamColor.WHITE) {
            return true;
        } else return game.blackUsername() == null && color == ChessGame.TeamColor.BLACK;
    }

    private void validateJoinRequest (JoinRequest request) throws DataAccessException {
        if (request.playerColor() == null || request.authToken() == null || request.authToken().isBlank()){
            throw new BadRequestException("Error: bad request.");
        }

        try {
            gameDAO.getGame(request.gameID());
        } catch (DataAccessException ex) {
            throw new BadRequestException("Error: game does not exist.");
        }
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
