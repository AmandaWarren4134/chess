package service;

import dataaccess.GameDAO;

public class GameService {
    private final GameDAO gameDAO;

    public GameService () {
        this.gameDAO = new GameDAO();
    }



    public void clearGameData () {
        gameDAO.clearAllGames();
    }
}
