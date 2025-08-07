package ui;

import server.ServerFacade;
import websocket.WebSocketFacade;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import ui.EscapeSequences;

import java.util.Scanner;

public class Repl implements ServerMessageObserver {
    private final String serverUrl;
    private final ServerFacade server;
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
                case PRE_LOGIN -> {result = handlePreLogin(input);}
                case POST_LOGIN -> {result = handlePostLogin(input);}
                case GAMEPLAY -> {result = handleGameplay(input);}
            }
            if (result.isQuit()) {
                System.out.println("Goodbye!");
                return;
            }
        }
    }

    private CommandResult handlePreLogin(String input) throws Exception {
        CommandResult result = preLogin.eval(input);
        System.out.println(result.getMessage());
        if (result.goForward) {
            // Create WebSocketFacade
            if (webSocket == null) {
                webSocket = new WebSocketFacade(serverUrl, this);
            }

            postLogin = new PostLoginUI(server, webSocket, result.getAuthToken(), result.getUsername());
            menuState = State.POST_LOGIN;
        }
        return result;
    }

    private CommandResult handlePostLogin(String input) throws Exception {
        CommandResult result = postLogin.eval(input);
        System.out.println(result.getMessage());
        if (postLogin.isSignedOut()) {
            menuState = State.PRE_LOGIN;
            preLogin = new PreLoginUI(server);
        }
        if (result.goForward) {
            gameplay = new GameplayUI(server, webSocket, result.getAuthToken(), result.getGameID(), null);
            menuState = State.GAMEPLAY;
        }
        return result;
    }

    private CommandResult handleGameplay(String input) throws Exception {
        CommandResult result =  gameplay.eval(input);
        System.out.println(result.getMessage());
        if (result.goForward) {
            menuState = State.POST_LOGIN;
        }
        return result;
    }

    @Override
    public void notify(LoadGameMessage message) {
        if (menuState == State.GAMEPLAY && gameplay != null) {
            gameplay.notify(message);
            System.out.print(">>> ");
        } else {
            System.out.println("Received LoadGameMessage but not yet in gameplay.");
        }
    }

    @Override
    public void notify(NotificationMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "\n>> " + message.getMessage());
        System.out.print(">>> ");
    }

    @Override
    public void notify(ErrorMessage message) {
        System.err.println(EscapeSequences.SET_TEXT_COLOR_RED + "\n>>" + message.getErrorMessage());
        System.out.print(">>> ");
    }
}
