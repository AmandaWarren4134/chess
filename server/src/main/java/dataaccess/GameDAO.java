package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

public class GameDAO implements IGameDAO {
    private final Map<Integer, GameData> gameList = new HashMap<>();

    /***
     * Adds a new game with the given name to the hashmap
     *
     * @param gameName
     */
    @Override
    public int createGame(String gameName) throws DataAccessException{
        Random random = new Random();
        int gameID = random.nextInt(Integer.MAX_VALUE) + 1;
        ChessGame game = new ChessGame();

        GameData newGame = new GameData(gameID, null, null, gameName, game);
        gameList.put(gameID, newGame);

        return gameID;
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
        if (!gameList.containsKey(gameID)) {
            throw new DataAccessException("Game with ID " + gameID + " does not exist.");
        }
        return gameList.get(gameID);
    }

    /***
     * Returns the values of the gameList hashmap as an ArrayList
     *
     * @return
     */
    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(gameList.values());
    }

    /***
     * Updates the game in the hashmap by replacing its value at the gameID key
     *
     * @param gameID
     * @param game
     * @throws DataAccessException
     */
    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (!gameList.containsKey(gameID)) {
            throw new DataAccessException("Game with ID " + gameID + " does not exist.");
        }
        gameList.put(gameID, game);
    }

    @Override
    public void clearAllGames()throws DataAccessException  {
        gameList.clear();
        if (! gameList.isEmpty()) {
            throw new DataAccessException("Failed to clear games.");
        }
    }
}
