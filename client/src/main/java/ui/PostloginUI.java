package ui;

import java.util.Arrays;

import exception.ResponseException;
import request.*;
import response.*;
import server.ServerFacade;

public class PostloginUI {
    private final ServerFacade server;
    private String authToken;
    private String username;
    private State state;

    public PostloginUI(ServerFacade server, String authToken, String username) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
    }

    public CommandResult eval(String input) {
        try {
            input = input.trim();
            var tokens = input.split("\\s+");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "list" -> list();
                case "create" -> create(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "help" -> help();
                case "quit" -> new CommandResult(true, "Quitting chess...", false, true);
                default -> new CommandResult(false, "Unknown command, type \"help\" to see more commands.", false, false);
            };
        } catch (ResponseException ex) {
            return new CommandResult(false, ex.getMessage(), false, false);
        }
    }

    private CommandResult logout() throws ResponseException {
        var request = new LogoutRequest(authToken);
        LogoutResult result = server.logout(request);

        this.state = State.SIGNEDOUT;
        this.authToken = null;
        this.username = null;
        return new CommandResult(true, "Logged out successfully.\n", false, false);
    }

    private CommandResult list() throws ResponseException {
        var request = new ListRequest(authToken);
        ListResult result = server.list(request);

        return new CommandResult(true, "Here are the current games: \n", false, false);
    }

    private CommandResult create(String[] params) throws ResponseException {
        if (params.length != 1) {
            return new CommandResult(false, "Usage: create <name>", false, false);
        }
        var gameName = params[0];
        var request = new CreateRequest(gameName, authToken);
        CreateResult result = server.create(request);

        if( result != null && result.gameID() > 0) {
            return new CommandResult(true, "Successfully created game " + result.gameID() + ".\n", false, false);
        } else {
            return new CommandResult(false, "Failed to create new game.\n", false, false);
        }
    }

    private CommandResult join(String[] params) throws ResponseException {
        if (params.length != 2) {
            return new CommandResult(false, "Usage: join <id> [WHITE|BLACK]", false, false);

        }
    }

    public CommandResult help() {
        return new CommandResult(true, """
                - create <name> - create a new game
                - list - print a list of current games
                - join <id> [WHITE|BLACK] - join an existing game
                - observe <id> - watch a game
                - logout - exit your account
                - quit - quit playing chess
                - help - get information about possible commands
                """, false, false);
    }
}
