package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage message);
//    void notify(LoadGameMessage message);
//    void notify(NotificationMessage message);
//    void notify(ErrorMessage message);
}
