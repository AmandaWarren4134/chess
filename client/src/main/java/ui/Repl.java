package ui;

import server.ServerFacade;
import websocket.WebSocketFacade;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

public class Repl implements ServerMessageObserver {
    private final String serverUrl;
    private ServerFacade server;
    private WebSocketFacade webSocket;
    private final Scanner scanner = new Scanner(System.in);

    private PreLoginUI preLogin;
    private PostLoginUI postLogin;
    private GameplayUI gameplay;

    public Repl(String serverUrl) throws Exception {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.preLogin = new PreLoginUI(server);
    }

    public void run() throws Exception {
        // preLogin menu loop
        while (true) {
            System.out.print(">>> ");
            var preInput = scanner.nextLine();
            CommandResult result;
            result = preLogin.eval(preInput);
            System.out.println(result.getMessage());

            if (result.goForward) {
                // Enter postLogin menu loop
                postLogin = new PostLoginUI(server, result.getAuthToken(), result.getUsername());

                // Create WebSocketFacade
                if (webSocket == null) {
                    webSocket = new WebSocketFacade(serverUrl, this);
                }

                while (true) {
                    System.out.print(">>> ");
                    var postInput = scanner.nextLine();
                    result = postLogin.eval(postInput);
                    System.out.println(result.getMessage());

                    if (result.isQuit()) {
                        System.out.println("Goodbye!");
                        break;
                    }

                    if (postLogin.isSignedOut()) {
                        break;
                    }

                    if (result.goForward) {
                        gameplay = new GameplayUI(serverUrl, result.getAuthToken(), result.getUsername(), result.getGameID(), null);
                        gameplay.setServerFacade(server);
                        while (true) {
                            System.out.print(">>> ");
                            var gameInput = scanner.nextLine();
                            result = gameplay.eval(gameInput);
                            System.out.println(result.getMessage());

                            if (result.isQuit()) {
                                System.out.println("Goodbye!");
                                break;
                            }
                        }
                    }
                }
            }
            if (result.isQuit()) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    @Override
    public void notify(LoadGameMessage message) {

    }

    @Override
    public void notify(NotificationMessage message) {

    }

    @Override
    public void notify(ErrorMessage message) {

    }
}
