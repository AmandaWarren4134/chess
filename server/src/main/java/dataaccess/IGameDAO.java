package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public interface IGameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData game) throws DataAccessException;
    void clearAllGames() throws DataAccessException;
}
