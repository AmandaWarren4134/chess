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
        var prelogin = new PreLoginUI(server);
        CommandResult result;

        while (true) {
            System.out.print(">>> ");
            var preInput = scanner.nextLine();
            result = prelogin.eval(preInput);
            System.out.println(result.getMessage());

            if (result.isQuit()) {
                System.out.println("Goodbye!");
                break;
            }

            if (result.goToPostLogin) {
                var postlogin = new PostLoginUI(server, prelogin.getAuthToken(), prelogin.getUsername());
                while (true) {
                    System.out.print(">>> ");
                    var postInput = scanner.nextLine();
                    result = postlogin.eval(postInput);
                    System.out.println(result.getMessage());

                    if (result.isQuit()) {
                        System.out.println("Goodbye!");
                        break;
                    }

                    if (postlogin.isSignedOut()) {
                        break;
                    }
                }


            }

        }

    }
}
