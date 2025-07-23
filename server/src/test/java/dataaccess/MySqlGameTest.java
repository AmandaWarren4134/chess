package dataaccess;

import dataaccess.MySqlGame;
import dataaccess.IGameDAO;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MySqlGameTest {

    private static MySqlGame gameDao;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDao = new MySqlGame();
        gameDao.clearAllGames();
    }

    @AfterEach
    void clearDatabaseAfterEach() throws DataAccessException {
        gameDao.clearAllGames();
    }

    @Test
    @DisplayName("Positive createGame")
    void createGame() throws DataAccessException {
        String gameName = "newGame";

        int gameID = gameDao.createGame(gameName);


    }

    @Test
    void getGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void updateGame() {
    }
}