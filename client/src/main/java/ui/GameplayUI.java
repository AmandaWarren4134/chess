package ui;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import chess.*;

import java.util.Arrays;
import java.util.Collection;

public class GameplayUI {
    private final ServerFacade server;
    private String authToken;
    private String username;
    private State state;
    private ChessGame game;

    public GameplayUI(ServerFacade server, String authToken, String username, GameData currentGame) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
        this.state = State.SIGNEDIN;
        this.game = currentGame.game();

        this.server.setAuthToken(authToken);
    }

    public CommandResult eval(String input) throws ResponseException {
        try {
            input = input.trim();
            var tokens = input.split("\\s+");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "help" -> help();
                case "quit" -> new CommandResult(true, "Exiting back to main menu...", false, true);
                default -> new CommandResult(false, "Unknown command, type \"help\" to see more commands.", false, false);
            }
        }
    }
    public CommandResult highlight(String [] params) throws ResponseException {
        if (params.length != 1) {
            return new CommandResult(false, "Usage: highlight <squarePosition>", false, false);
        }

        String startSquare = params[0];
        ChessPosition startPosition = translateToChessPosition(startSquare);

        ChessGame currentGame = game;
        Collection<ChessMove> validMoves = game.validMoves(startPosition);

    }

    private ChessPosition translateToChessPosition(String startSquare) {
        if (startSquare.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + startSquare);
        }

        char colChar = Character.toLowerCase(startSquare.charAt(0));
        char rowChar = startSquare.charAt(1);

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid Square: " + startSquare);
        }
        return new ChessPosition(row, col);
    }
    public CommandResult help() {
        return new CommandResult(true, """
                - redraw - redraw the chess board
                - leave - leave this game
                - move <startPosition> <endPosition> - make a move, e.g. move a2 a3
                - resign - forfeit the game
                - highlight <squarePosition> - highlight all legal moves
                - quit - quit playing chess
                - help - get information about possible commands
                """, false, false);
    }
}
