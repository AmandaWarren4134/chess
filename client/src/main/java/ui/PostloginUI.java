package ui;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import request.*;
import response.*;
import server.ServerFacade;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class PostloginUI {
    private final ServerFacade server;
    private String authToken;
    private String username;
    private State state;
    private ArrayList<GameData> lastGameList = new ArrayList<>();

    public PostloginUI(ServerFacade server, String authToken, String username) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
        this.state = State.SIGNEDIN;

        this.server.setAuthToken(authToken);
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
        server.setAuthToken(null);

        return new CommandResult(true, "Logged out successfully.\n", false, false);
    }

    private CommandResult list() throws ResponseException {
        var request = new ListRequest(authToken);
        ListResult result = server.list(request);
        lastGameList = result.games();

        StringBuilder output = new StringBuilder("Here are the current games:\n");
        for (int i = 0; i < lastGameList.size(); i++) {
            GameData game = lastGameList.get(i);
            output.append(String.format("(%d) %s (White: %s, Black: %s)\n",
                    i + 1,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "---",
                    game.blackUsername() != null ? game.blackUsername() : "---"));
        }
        return new CommandResult(true, output.toString(), false, false);
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

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) -1;
        } catch (NumberFormatException e) {
            return new CommandResult(false, "Game number must be a number.", false, false);
        }

        if (gameNumber < 0 || gameNumber >= lastGameList.size()) {
            return new CommandResult(false, "Invalid Game Number.", false, false);
        }

        int gameID = lastGameList.get(gameNumber).gameID();

        ChessGame.TeamColor teamColor;
        try {
            teamColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResult(false, "Color must be WHITE or BLACK.", false, false);
        }

        var request = new JoinRequest(authToken, teamColor, gameID);
        try {
            server.join(request);
            ChessBoard board = new ChessBoard();
            ChessBoardPrinter printer = new ChessBoardPrinter();
            printer.print(board, teamColor);

            return new CommandResult(true, "Successfully joined game " + gameNumber + ".\n", false, false);
        } catch (ResponseException e) {
            return new CommandResult(false, e.getMessage(), false, false);
        }
    }

    private CommandResult observe(String [] params) throws ResponseException {
        if (params.length != 1) {
            return new CommandResult(false, "Usage: observe <id>", false, false);
        }

        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return new CommandResult(false, "Game ID must be a number.", false, false);
        }

        ChessBoard board = new ChessBoard();
        ChessBoardPrinter printer = new ChessBoardPrinter();
        printer.print(board, WHITE);

        return new CommandResult(true, "Displaying game " + gameID + ".\n", false, false);
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

    public boolean isSignedOut() {
        return state == State.SIGNEDOUT;
    }
}
