package ui;

import server.ServerFacade;

import java.util.Scanner;

public class Repl {
    private final String serverUrl;
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public void run() {
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
                var postLogin = new PostLoginUI(server, prelogin.getAuthToken(), prelogin.getUsername());
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
                        var gameplay = new GameplayUI(server, postLogin.getAuthToken(), postLogin.getUsername(), postLogin.getTeamColor(), postLogin.getGameData());
                    }
                }


            }

        }

    }
}
