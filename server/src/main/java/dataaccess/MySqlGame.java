package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class MySqlGame implements IGameDAO {
    private static final Gson gson = new Gson();

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);

        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO game (name, gameState) VALUES (?,?)";
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, gameName);
                ps.setString(2, gameJson);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        throw new DataAccessException("Failed to retrieve generated game ID.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteUsername, blackUsername, gameName, gameState FROM game WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                    else {
                        throw new DataAccessException("No game found with ID: " + gameID);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
    }

    private GameData readGame (ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameStateJson = rs.getString("gameState");

        ChessGame game = gson.fromJson(gameStateJson, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }


    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to list games.", e);
        }
        return result;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

        try(var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET gameState = ? WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                String gameJson = gson.toJson(gameData.game());
                ps.setString(1, gameJson);
                ps.setInt(2, gameID);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("No game found with ID: " + gameID);
                }

            }
        } catch (SQLException e ) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        String statement = "DELETE FROM game";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to clear games", e);
        }
    }
}
