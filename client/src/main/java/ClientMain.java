import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to Chess! Type 'help' to view commands.");
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new Repl(serverUrl).run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}