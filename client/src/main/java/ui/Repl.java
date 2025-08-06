package ui;

import server.ServerFacade;
import websocket.ServerMessageObserver;

import java.util.Scanner;

public class Repl {
    private final String serverUrl;
    private ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(String serverUrl) throws Exception {
        this.serverUrl = serverUrl;
    }

    public void run() throws Exception {
        var preLogin = new PreLoginUI(server);
        CommandResult result;

        while (true) {
            System.out.print(">>> ");
            var preInput = scanner.nextLine();
            result = preLogin.eval(preInput);
            System.out.println(result.getMessage());

            if (result.isQuit()) {
                System.out.println("Goodbye!");
                break;
            }

            if (result.goForward) {
                var gameplay = new GameplayUI(null, result.getAuthToken(), result.getUsername(), result.getGameID, null);
                server = new ServerFacade(serverUrl, gameplay);
                gameplay.setServerFacade(server);

                var postLogin = new PostLoginUI(server, result.getAuthToken(), result.getUsername());
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

        }

    }
}
