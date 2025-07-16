package dataaccess;

import Model.GameData;
import chess.ChessGame;

import java.util.ArrayList;

public interface IGameDAO {
    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames();
    void updateGame(String username, ChessGame.TeamColor playerColor, int gameID, ChessGame game) throws DataAccessException;

}
