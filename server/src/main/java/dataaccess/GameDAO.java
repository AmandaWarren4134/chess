package dataaccess;

import Model.GameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class GameDAO {
    private final Map<Integer, GameData> gameList = new HashMap<>();

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        gameList.put(gameID, newGame);
    }
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = gameList.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game " + gameID + " not found.");
        }
        return gameData;
    }
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameList.values());
    }
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID, ChessGame game) throws DataAccessException {
        GameData gameData = getGame(gameID);

        GameData updatedGameData = gameData.update(username, playerColor, game);
        gameList.put(gameID, updatedGameData);
    }
}
