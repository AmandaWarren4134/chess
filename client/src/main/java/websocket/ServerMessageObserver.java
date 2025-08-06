package websocket;

import websocket.messages.*;

public interface ServerMessageObserver {
    void notify(LoadGameMessage message);
    void notify(NotificationMessage message);
    void notify(ErrorMessage message);
}
