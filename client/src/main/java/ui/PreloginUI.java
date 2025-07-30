package ui;

import java.util.Arrays;

import exception.ResponseException;
import request.*;
import response.*;
import server.ServerFacade;

public class PreloginUI {
    private final ServerFacade server;
    private String authToken;
    private String username;
    private State state = State.SIGNEDOUT;

    public PreloginUI(ServerFacade server) {
        this.server = server;
    }

    public CommandResult eval(String input) {
        try {
            input = input.trim();
            var tokens = input.split("\\s+");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> new CommandResult(true, "Quitting chess...", false, true);
                case "help" -> help();
                default -> new CommandResult(false, "Unknown command, type \"help\" to see more commands.", false, false);
            };
        } catch (ResponseException ex) {
            return new CommandResult(false, ex.getMessage(), false, false);
        }
    }

    private CommandResult register(String[] params) throws ResponseException {
        if (params.length != 3) {
            return new CommandResult(false, "Usage: register <username> <password> <email>", false, false);
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];

        var request = new RegisterRequest(username, password, email);
        RegisterResult result = server.register(request);

        if (result.authToken() != null && result.username() != null) {
            this.state = State.SIGNEDIN;
            this.authToken = result.authToken();
            server.setAuthToken(this.authToken);
            this.username = result.username();
            return new CommandResult(true, "Registered as " + username + ".\n", true, false);
        } else {
            server.setAuthToken(null);
            return new CommandResult (false, "Registration failed. Please try again.", false, false);
        }
    }

    private CommandResult login (String[] params) throws ResponseException {
        if (params.length != 2) {
            return new CommandResult(false, "Usage: login <username> <password>", false, false);
        }
        var username = params[0];
        var password = params[1];

        var request = new LoginRequest(username, password);
        LoginResult result = server.login(request);

        if (result.authToken() != null && result.username() != null) {
            this.state = State.SIGNEDIN;
            this.authToken = result.authToken();
            server.setAuthToken(this.authToken);
            this.username = result.username();
            return new CommandResult(true, "Logged in as " + username + ".\n", true, false);
        } else {
            return new CommandResult(false, "Login failed. Please try again.", false, false);
        }
    }

    public CommandResult help() {
        return new CommandResult(true, """
                - register <username> <password> <email> - create an account
                - login <username> <password> - login to an existing account
                - quit - quit playing chess
                - help - get information about possible commands
                """, false, false);
    }

    public boolean isSignedIn() {
        return state == State.SIGNEDIN;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
