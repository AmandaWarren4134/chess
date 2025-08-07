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

    private enum State { PRE_LOGIN, POST_LOGIN, GAMEPLAY}
    private State menuState = State.PRE_LOGIN;

    public Repl(String serverUrl) throws Exception {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.preLogin = new PreLoginUI(server);
    }

    public void run() throws Exception {
        // main loop
        while (true) {
            System.out.print(">>> ");
            String input = scanner.nextLine();
            CommandResult result = null;

            switch (menuState) {
                // Enter postLogin menu loop
                case PRE_LOGIN -> {
                    result = preLogin.eval(input);
                    System.out.println(result.getMessage());

                    if (result.goForward) {
                        // Create WebSocketFacade
                        if (webSocket == null) {
                            webSocket = new WebSocketFacade(serverUrl, this);
                        }

                        postLogin = new PostLoginUI(server, webSocket, result.getAuthToken(), result.getUsername());
                        menuState = State.POST_LOGIN;
                    }
                    if (result.isQuit()) {
                        System.out.println("Goodbye!");
                        return;
                    }
                }
                // Enter PostLogin loop
                case POST_LOGIN -> {
                    System.out.println("You're logged in! Type \"help\" to view available commands.");
                    result = postLogin.eval(input);
                    System.out.println(result.getMessage());

                    if (result.isQuit()) {
                        System.out.println("Goodbye!");
                        return;
                    }

                    if (postLogin.isSignedOut()) {
                        menuState = State.PRE_LOGIN;
                        preLogin = new PreLoginUI(server);
                    }
                    if (result.goForward) {
                        gameplay = new GameplayUI(server, webSocket, result.getAuthToken(), result.getUsername(), result.getGameID(), null);
                        menuState = State.GAMEPLAY;
                    }
                }
                // Enter gameplay loop
                case GAMEPLAY -> {
                    System.out.println("Welcome to the game! Type \"help\" to view available commands.");
                    result = gameplay.eval(input);
                    System.out.println(result.getMessage());

                    if (result.isQuit()) {
                        System.out.println("Goodbye!");
                        return;
                    }
                    if (result.goForward) {
                        menuState = State.POST_LOGIN;
                    }
                }
            }
        }
    }

    @Override
    public void notify(LoadGameMessage message) {
        if (menuState == State.GAMEPLAY && gameplay != null) {
            gameplay.notify(message);
        } else {
            System.out.println("Received LoadGameMessage but not yet in gameplay.");
        }
    }

    @Override
    public void notify(NotificationMessage message) {
        System.out.println(">> " + message.getMessage());
    }

    @Override
    public void notify(ErrorMessage message) {
        System.err.println("Error: " + message.getErrorMessage());
    }
}
