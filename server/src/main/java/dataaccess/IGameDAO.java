package dataaccess;

import Model.GameData;
import chess.ChessGame;

import java.util.ArrayList;

public interface IGameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames();
    void updateGame(int gameID, GameData game) throws DataAccessException;
}
