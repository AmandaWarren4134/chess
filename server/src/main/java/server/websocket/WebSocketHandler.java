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
import websocket.messages.ServerMessage;

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
            throw new IOException("Error: Unable to validate the connect request.");
        }
        connections.add(authToken, session, gameID);
        ServerMessage loadMessage = new ServerMessage(LOAD_GAME);
        loadMessage.
    }

    private void move() throws IOException {

    }

    private void leave() throws IOException {

    }

    private void resign() throws IOException {

    }
}
