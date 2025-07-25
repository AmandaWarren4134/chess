package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.MySqlGame;
import dataaccess.IGameDAO;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MySqlGameTest {

    private static IGameDAO gameDao;

    @BeforeEach
    void setup() throws DataAccessException {
        try {
            DatabaseManager.configureDatabase();
            gameDao = new MySqlGame();
        } catch (DataAccessException e) {
            System.out.print("GameTest: Database configuration failed, using memory DAOs.");
            gameDao = new GameDAO();
        }
        // clear the database or memory
        gameDao.clearAllGames();
    }

    @AfterEach
    void clearDatabaseAfterEach() throws DataAccessException {
        gameDao.clearAllGames();
    }

    @Test
    @DisplayName("Positive createGame")
    void createGame() throws DataAccessException {
        // Create a new game
        String gameName = "newGame";
        int gameID = gameDao.createGame(gameName);

        // Make sure the gameID is not zero
        assertNotEquals(0, gameID);

        // Verify that getGame(gameID) matches
        GameData gameData = gameDao.getGame(gameID);
        assertEquals(gameName, gameData.gameName());
        assertNotNull(gameData.game());
    }

    @Test
    @DisplayName("Negative createGame")
    void createGameInvalidInputThrowsException() {
        // Attempt to create a game with bad input
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameDao.createGame(null);
        });
        System.out.print("Exception message: " + ex.getMessage());
    }

    @Test
    @DisplayName("Positive getGame")
    void getGame() throws DataAccessException{
        // Create a new game
        String gameName = "newGame";
        int gameID = gameDao.createGame(gameName);

        // Verify that getGame(gameID) matches
        GameData gameData = gameDao.getGame(gameID);
        assertEquals(gameName, gameData.gameName());
        assertNotNull(gameData.game());
    }

    @Test
    @DisplayName("Negative getGame")
    void getInvalidGameID() throws DataAccessException{
        // Attempt to get a game with an ID that doesn't exist
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameDao.getGame(123456);
        });
        System.out.print("Exception message: " + ex.getMessage());
    }

    @Test
    @DisplayName("Positive listGames")
    void listGames() throws DataAccessException{
        // Create some games
        gameDao.createGame("game1");
        gameDao.createGame("game2");
        gameDao.createGame("game3");

        // List the games
        ArrayList<GameData> games = gameDao.listGames();

        // Assertions
        assertEquals(3, games.size());
        assertEquals("game1", games.get(0).gameName());
        assertEquals("game2", games.get(1).gameName());
        assertEquals("game3", games.get(2).gameName());
    }

    @Test
    @DisplayName("listGames returns empty list when no games created")
    void listNoGames() throws DataAccessException{
        gameDao.clearAllGames();

        ArrayList<GameData> games = gameDao.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    @DisplayName("Positive updateGame")
    void updateGame() throws DataAccessException {
        // Create a game
        int firstID = gameDao.createGame("game1");

        // Get the gameData
        GameData gameData = gameDao.getGame(firstID);

        // Modify the ChessGame
        ChessGame myGame = gameDao.getGame(firstID).game();
        Collection<ChessMove> myValidMoves = myGame.validMoves(new ChessPosition(2, 4));
        ChessMove myMove = myValidMoves.iterator().next();

        try {
            myGame.makeMove(myMove);
        }
        catch (InvalidMoveException e){
            throw new DataAccessException ("Invalid move.", e);
        }

        // Update the game
        gameDao.updateGame(firstID, new GameData(firstID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), myGame));

        // Get the game again
        GameData updatedGameData = gameDao.getGame(firstID);

        assertNotEquals(gameData.game(), updatedGameData.game());
        assertNotEquals(gameData.game().getBoard().toString(),
                updatedGameData.game().getBoard().toString(),
                "Expected game board to be updated"
        );
    }

    @Test
    @DisplayName("Update Game that does not exist")
    void updateInvalidGameID () throws DataAccessException {
        // Create a game
        int firstID = gameDao.createGame("game1");

        // Get the gameData
        GameData gameData = gameDao.getGame(firstID);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameDao.updateGame(9999, gameData);
        });
        System.out.print("Exception message: " + ex.getMessage());
    }

    @Test
    void clearAllGames() throws Exception {
        // Put games in the database
        int firstID = gameDao.createGame("game1");
        int secondID = gameDao.createGame("game2");
        int thirdID = gameDao.createGame("game3");


        // Make sure they exist
        assertNotEquals(0, firstID);
        assertNotEquals(0, secondID);
        assertNotEquals(0, thirdID);

        // Clear games
        gameDao.clearAllGames();

        // Check database
        assertThrows(DataAccessException.class, () -> gameDao.getGame(firstID));
        assertThrows(DataAccessException.class, () -> gameDao.getGame(secondID));
        assertThrows(DataAccessException.class, () -> gameDao.getGame(thirdID));
    }
}