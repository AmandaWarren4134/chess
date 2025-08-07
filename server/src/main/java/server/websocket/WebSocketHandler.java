package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.IAuthDAO;
import dataaccess.IGameDAO;
import dataaccess.MySqlAuth;
import dataaccess.MySqlGame;
import dataaccess.exceptions.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final IAuthDAO authDAO = new MySqlAuth();
    private final IGameDAO gameDAO = new MySqlGame();
    private final Gson serializer = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {

            UserGameCommand baseCommand = serializer.fromJson(message, UserGameCommand.class);

            UserGameCommand command;

            // Deserialize to check command type
            switch (baseCommand.getCommandType()) {
                case CONNECT -> command = serializer.fromJson(message, ConnectCommand.class);
                case MAKE_MOVE -> command = serializer.fromJson(message, MakeMoveCommand.class);
                case LEAVE -> command = serializer.fromJson(message, LeaveGameCommand.class);
                case RESIGN -> command = serializer.fromJson(message, ResignCommand.class);
                default -> throw new IllegalArgumentException("Unknown command type.");
            }

            String username = authDAO.getAuth(command.getAuthToken()).username();
            GameData gameData = gameDAO.getGame(command.getGameID());

            connections.add(command.getAuthToken(), session, command.getGameID());

            // Deserialize again into specific command
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), username, command.getGameID(), gameData, session);
                case MAKE_MOVE ->
                        move(command.getAuthToken(), username, command.getGameID(), gameData, ((MakeMoveCommand) command).getMove(), session);
                case LEAVE -> leave(command.getAuthToken(), username, command.getGameID(), gameData, session);
                case RESIGN -> resign(command.getAuthToken(), username, command.getGameID(), gameData, session);
            }
        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid authToken or gameID.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (Exception ex) {
            ex.printStackTrace();
            session.getRemote().sendString(serializer.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void connect(String authToken, String username, int gameID, GameData gameData, Session session) throws IOException {
        // Send LOAD_GAME to client
        LoadGameMessage gameMessage = new LoadGameMessage(gameData);
        session.getRemote().sendString(serializer.toJson(gameMessage));

        // Determine player team or if observer
        NotificationMessage notification = getNotificationMessage(username, gameData);

        // Broadcast to all other players
        connections.broadcastExcept(authToken, gameID, notification);
    }

    private static NotificationMessage getNotificationMessage(String username, GameData gameData) {
        String team;
        if (username.equals(gameData.whiteUsername())) {
            team = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            team = "BLACK";
        } else {
            team = "OBSERVER";
        }

        // Notify other clients
        String notificationText;
        if (team.equals("OBSERVER")) {
            notificationText = username + " joined the game as an observer.";
        } else {
            notificationText = username + " joined the game as " + team + ".";
        }

        return new NotificationMessage(notificationText);
    }

    private static ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            return BLACK;
        } else {
            return null;
        }
    }

    private void move(String authToken, String username, Integer gameID, GameData gameData, ChessMove move, Session session) throws IOException {
        // Validate user and turn
        ChessGame game = gameData.game();
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
        if (playerColor == null) {
            ErrorMessage errorMessage = new ErrorMessage("Cannot move, you are not a player in this game.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
            return;
        } else if (playerColor != turnColor) {
            ErrorMessage errorMessage = new ErrorMessage("Out of turn. It is currently " + turnColor + "'s turn.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
            return;
        }

        // Apply the move
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
            return;
        }

        // Persist updated game
        try {
            gameDAO.updateGame(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }

        // Broadcast new game state
        LoadGameMessage gameMessage = new LoadGameMessage(gameData);
        connections.broadcast(gameID, gameMessage);

        // Notify other players of the move
        String moveText = username + " moved " + translateFromChessPosition(move.getStartPosition())
                + " to " + translateFromChessPosition(move.getEndPosition()) + ".";
        NotificationMessage notification = new NotificationMessage(moveText);
        connections.broadcastExcept(authToken, gameID, notification);

        // Check for check, checkmate, stalemate and notify
        if (game.isInCheckmate(game.getTeamTurn())) {
            game.setGameOver(true);
            String checkmateText = game.getTeamTurn() + " is in checkmate.";
            NotificationMessage checkmate = new NotificationMessage(checkmateText);
            connections.broadcast(gameID, checkmate);
        } else if (game.isInCheck(game.getTeamTurn())) {
            String checkText = game.getTeamTurn() + " is in check.";
            NotificationMessage check = new NotificationMessage(checkText);
            connections.broadcast(gameID, check);
        }
        if (game.isInStalemate(game.getTeamTurn())) {
            game.setGameOver(true);
            String stalemateText = "The game is at a stalemate";
            NotificationMessage stalemate = new NotificationMessage(stalemateText);
            connections.broadcast(gameID, stalemate);
        }
    }

    private String translateFromChessPosition(ChessPosition position) {
        int col = position.getColumn();
        char colChar = (char) ('a' + (col - 1));
        return String.valueOf(colChar) + position.getRow();
    }

    private void leave(String authToken, String username, Integer gameID, GameData gameData, Session session) throws IOException {
        // If the user is a player, update GameData
        if (username.equals(gameData.whiteUsername()) ||
                username.equals(gameData.blackUsername()) ) {
            // Update game
            gameData = gameData.update(null, getPlayerColor(username, gameData), gameData.game());

            // Persist updated game
            try {
                gameDAO.updateGame(gameData.gameID(), gameData);
            } catch (DataAccessException e) {
                ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(serializer.toJson(errorMessage));
                return;
            }
        }

        // Remove the user from WebSocket connections
        connections.removeFromGame(gameID, authToken);

        // Notify all other clients
        String leaveText = username + " left the game.";
        NotificationMessage leaveMessage = new NotificationMessage(leaveText);
        connections.broadcastExcept(authToken, gameID, leaveMessage);
    }

    private void resign(String authToken, String username, Integer gameID, GameData gameData, Session session) throws IOException {
        // Validate that the user is a player
        if (!username.equals(gameData.whiteUsername()) &&
                !username.equals(gameData.blackUsername()) ) {
            ErrorMessage errorMessage = new ErrorMessage("Cannot resign if you are not a player.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
            return;
        }

        // Check if the game is over
        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            session.getRemote().sendString(serializer.toJson(new ErrorMessage("Cannot resign if the game is over.")));
            return;
        }
        // Mark game as over
        game.setGameOver(true);

        // Persist the updated game
        try {
            gameDAO.updateGame(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Failed to update game state.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
            return;
        }

        // Notify all clients
        String resignText = username + " resigned from the game.";
        NotificationMessage resignMessage = new NotificationMessage(resignText);
        connections.broadcast(gameID, resignMessage);

    }
}
