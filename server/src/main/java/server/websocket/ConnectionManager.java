package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // Map of authToken to Connection
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    // Map of gameID to set of authTokens for players
    private final ConcurrentHashMap<Integer, Set<String>> gameConnections = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    public void add(String authToken, Session session, int gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameConnections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(authToken);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(int gameID, Object message) throws IOException {
        String json = gson.toJson(message);

        Set<String> recipients = gameConnections.get(gameID);
        if (recipients != null) {
            for (String authToken : recipients) {
                Connection conn = connections.get(authToken);
                if (conn != null) {
                    conn.send(json);
                }
            }
        }
    }

    public void broadcastExcept(String excludedAuthToken, int gameID, Object message) throws IOException {
        String json = gson.toJson(message);

        Set<String> recipients = gameConnections.get(gameID);
        if (recipients != null) {
            for (String authToken : recipients) {
                if (!authToken.equals(excludedAuthToken)) {
                    Connection conn = connections.get(authToken);
                    if (conn != null) {
                        conn.send(json);
                    }
                }
            }
        }
    }
}
