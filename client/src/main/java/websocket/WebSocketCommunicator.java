package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;
    ServerMessageObserver observer;

    public WebSocketCommunicator(String url, ServerMessageObserver observer) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String messageJson) {
                    Gson gson = new Gson();
                    ServerMessage baseMessage = gson.fromJson(messageJson, ServerMessage.class);

                    switch (baseMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadMsg = gson.fromJson(messageJson, LoadGameMessage.class);
                            observer.notify(loadMsg);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notification = gson.fromJson(messageJson, NotificationMessage.class);
                            observer.notify(notification);
                        }
                        case ERROR -> {
                            ErrorMessage error = gson.fromJson(messageJson, ErrorMessage.class);
                            observer.notify(error);
                        }
                        default -> {
                            System.out.println("Unknown message type: " + baseMessage.getServerMessageType());
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void send(UserGameCommand command) throws ResponseException {
        try {
            String json = new Gson().toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void close() throws IOException {
        session.close();
    }
}
