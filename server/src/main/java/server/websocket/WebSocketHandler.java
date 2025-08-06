package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.IAuthDAO;
import dataaccess.IGameDAO;
import dataaccess.exceptions.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private IAuthDAO authDAO;
    private IGameDAO gameDAO;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket disconnected: " + session + " (" + reason + ")");
        connections.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> move(command.getAuthToken(), command.getGameID(), ((MakeMoveCommand) command).getMove(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        // Validate the request
        String username;
        GameData gameData;

        try {
            AuthData authData = authDAO.getAuth(authToken);
            username = authData.username();
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid authToken or gameID");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // Register connection
        connections.add(authToken, session, gameID);

        // Send LOAD_GAME to client
        LoadGameMessage gameMessage = new LoadGameMessage(gameData);
        session.getRemote().sendString(new Gson().toJson(gameMessage));

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
            notificationText = username + "joined the game as an observer.";
        } else {
            notificationText = username + "joined the game as " + team + ".";
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

    private void move(String authToken, Integer gameID, ChessMove move, Session session) throws IOException {
        // Validate the request
        String username;
        GameData gameData;

        try {
            AuthData authData = authDAO.getAuth(authToken);
            username = authData.username();
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid authToken or gameID.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // Validate user and turn
        ChessGame game = gameData.game();
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
        if (playerColor == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid player.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        } else if (playerColor != turnColor) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid turn. It is currently " + turnColor + "'s turn.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // Apply the move
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // Persist updated game
        try {
            gameDAO.updateGame(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }

        // Broadcast new game state
        LoadGameMessage gameMessage = new LoadGameMessage(gameData);
        connections.broadcast(gameID, gameMessage);

        // Notify other players of the move
        String moveText = username + " moved " + move.getStartPosition() + " to " + move.getEndPosition() + ".";
        NotificationMessage notification = new NotificationMessage(moveText);
        connections.broadcastExcept(authToken, gameID, notification);

        // Check for check, checkmate, stalemate and notify
        if (game.isInCheck(game.getTeamTurn())) {
            String checkText = game.getTeamTurn() + " is in check.";
            NotificationMessage check = new NotificationMessage(checkText);
            connections.broadcast(gameID, check);
        }
        if (game.isInCheckmate(game.getTeamTurn())) {
            String checkmateText = game.getTeamTurn() + " is in checkmate.";
            NotificationMessage checkmate = new NotificationMessage(checkmateText);
            connections.broadcast(gameID, checkmate);
        }
        if (game.isInStalemate(game.getTeamTurn())) {
            String stalemateText = "The game is at a stalemate";
            NotificationMessage stalemate = new NotificationMessage(stalemateText);
            connections.broadcast(gameID, stalemate);
        }
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        String username;
        GameData gameData;

        try {
            AuthData authData = authDAO.getAuth(authToken);
            username = authData.username();
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid authToken or gameID.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // If the user is a player, update GameData
        if (username.equals(gameData.whiteUsername()) ||
                username.equals(gameData.blackUsername()) ) {
            // Update game
            gameData = gameData.update(null, getPlayerColor(username, gameData), gameData.game());

            // Persist updated game
            try {
                gameDAO.updateGame(gameData.gameID(), gameData);
            } catch (DataAccessException e) {
                ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage() + ".");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }
        }

        // Remove the user from WebSocket connections
        connections.remove(session);

        // Notify all other clients
        String leaveText = username + " left the game.";
        NotificationMessage leaveMessage = new NotificationMessage(leaveText);
        connections.broadcastExcept(authToken, gameID, leaveMessage);
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        String username;
        GameData gameData;

        try {
            AuthData authData = authDAO.getAuth(authToken);
            username = authData.username();
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid authToken or gameID.");
            session.getRemote().sendString(new Gson().toJson(errorMessage) + ".");
            return;
        }

        // Validate that the user is a player
        if (!username.equals(gameData.whiteUsername()) &&
                !username.equals(gameData.blackUsername()) ) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot resign if you are nota  player.");
            session.getRemote().sendString(new Gson().toJson(errorMessage) + ".");
            return;
        }

        // Mark game as over
        ChessGame game = gameData.game();
        game.setGameOver(true);

        // Persist the updated game
        try {
            gameDAO.updateGame(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Failed to update game state.");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        // Notify all clients
        String resignText = username + " left the game.";
        NotificationMessage resignMessage = new NotificationMessage(resignText);
        connections.broadcast(gameID, resignMessage);

    }
}
