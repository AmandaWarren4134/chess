package dataaccess;

import Model.GameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class GameDAO implements IGameDAO {
    private final Map<Integer, GameData> gameList = new HashMap<>();

    /***
     * Adds a new game with the given parameters to the hashmap
     *
     * @param gameID
     * @param whiteUsername
     * @param blackUsername
     * @param gameName
     * @param game
     */
    @Override
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        gameList.put(gameID, newGame);
    }

    /***
     * Returns a GameData object if the gameID is in the hashmap, otherwise throws a DataAccessException
     *
     * @param gameID
     * @return
     * @throws DataAccessException
     */
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = gameList.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game " + gameID + " not found.");
        }
        return gameData;
    }

    /***
     * Returns the values of the gameList hashmap as an ArrayList
     *
     * @return
     */
    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameList.values());
    }

    /***
     * Updates the game in the hashmap by replacing its value at the gameID key
     *
     * @param username
     * @param playerColor
     * @param gameID
     * @param game
     * @throws DataAccessException
     */
    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID, ChessGame game) throws DataAccessException {
        GameData gameData = getGame(gameID);

        GameData updatedGameData = gameData.update(username, playerColor, game);
        gameList.put(gameID, updatedGameData);
    }

    public void clearAllGames() {
        gameList.clear();
    }
}
