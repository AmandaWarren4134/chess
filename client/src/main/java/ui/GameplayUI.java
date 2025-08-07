package ui;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import chess.*;

import java.util.Arrays;
import java.util.Collection;

import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayUI {
    private ServerFacade server;
    private WebSocketFacade webSocket;
    private final String authToken;
    private String username;
    private final Integer gameID;
    private final ChessGame.TeamColor perspective;
    private ChessGame game;
    private final ChessBoardPrinter printer = new ChessBoardPrinter();

    public GameplayUI(ServerFacade server, WebSocketFacade webSocket, String authToken, String username, Integer gameID, ChessGame.TeamColor perspective) throws Exception {
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.perspective = perspective;

        this.server = server;
        this.server.setAuthToken(authToken);
        this.webSocket = webSocket;
    }

    public CommandResult eval(String input) {
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
                default -> new CommandResult(false, "Type \"help\" to see more commands.", false, false);
            };
        } catch (Exception e) {
            return new CommandResult(false, "Error processing command: " + e.getMessage(), false, false);
        }
    }

    private CommandResult redraw() {
        if (game == null) {
            return new CommandResult(false, "No game to redraw yet.", false, false);
        }
        drawChessBoard();

        return new CommandResult(true, "", false, false);
    }

    private CommandResult leave() {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocket.send(command);
            return new CommandResult(true, "You have left the game.", true, false);
        } catch (Exception e) {
            return new CommandResult(false, "Error leaving game: " + e.getMessage(), false, false);
        }
    }

    public CommandResult move(String[] params) {
        if (params.length != 2) {
            return new CommandResult(false, "Usage: move <startPosition> <endPosition>", false, false);
        }

        try {
            ChessPosition start = translateToChessPosition(params[0]);
            ChessPosition end = translateToChessPosition(params[1]);
            ChessMove move = new ChessMove(start, end, null);

            MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
            webSocket.send(command);

            return new CommandResult(true, "", false, false);
        } catch (Exception e) {
            return new CommandResult(false, "Invalid move: " + e.getMessage(), false, false);
        }
    }

    private CommandResult resign() {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            webSocket.send(command);
            return new CommandResult(true, "You have resigned.", false, false);
        } catch (Exception e){
            return new CommandResult(false, "Error resigning from the game: " + e.getMessage(), false, false);
        }
    }

    public CommandResult highlight(String[] params) {
        if (params.length != 1) {
            return new CommandResult(false, "Usage: highlight <squarePosition>", false, false);
        }

        try {
            ChessPosition startPosition = translateToChessPosition(params[0]);
            Collection<ChessMove> validMoves = game.validMoves(startPosition);

            if (validMoves == null || validMoves.isEmpty()) {
                return new CommandResult(true, "No valid moves from " + params[0], false, false);
            } else {
                printer.print(game.getBoard(), perspective, validMoves, startPosition);
                return new CommandResult(true, "", false, false);
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage(), false, false);
        }
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

    public void notify(LoadGameMessage message) {

        System.out.println("LoadGameMessage received");
        if (message.getGame() == null) {
            System.err.println("Warning: LoadGameMessage.getGame() is null");

        } else {
            this.game = message.getGame().game();
            printer.print(game.getBoard(), perspective);
        }
    }

    public void drawChessBoard() {
        if (game == null) {
            System.out.println("Game not loaded yet. Cannot draw board.");
            return;
        }
        printer.print(game.getBoard(), perspective);
    }
}
