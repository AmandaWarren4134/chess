package ui;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final String serverUrl;
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public void run() {
        var prelogin = new PreloginUI(server);
        CommandResult result;

        while (true) {
            System.out.print(">>> ");
            var input = scanner.nextLine();
            result = prelogin.eval(input);
            System.out.println(result.getMessage());

            if (result.isQuit()) {
                System.out.println("Goodbye!");
                break;
            }

            if (result.goToPostLogin) {
                var postlogin = new PostloginUI(server, prelogin.getAuthToken(), prelogin.getUsername());
                result = postlogin.run(scanner);

                if (!postlogin.isSignedIn()) {
                    prelogin = new PreloginUI(server);
                }
            }

        }

    }
}
