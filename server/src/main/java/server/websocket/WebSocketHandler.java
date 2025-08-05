package server.websocket;

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
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;

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
            case MAKE_MOVE -> move();
            case LEAVE -> leave();
            case RESIGN -> resign();
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
        connections.broadcast(gameID, notification);
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

    private void move() throws IOException {

    }

    private void leave() throws IOException {

    }

    private void resign() throws IOException {

    }
}
