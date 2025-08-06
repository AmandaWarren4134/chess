package ui;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import chess.*;

import java.util.Arrays;
import java.util.Collection;

import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayUI implements websocket.ServerMessageObserver {
    private ServerFacade server;
    private final String authToken;
    private String username;
    private final Integer gameID;
    private final ChessGame.TeamColor perspective;
    private final State state;
    private ChessGame game;

    public GameplayUI(ServerFacade server, String authToken, String username, Integer gameID, ChessGame.TeamColor perspective) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.state = State.SIGNEDIN;
        this.perspective = perspective;

        this.server.setAuthToken(authToken);
    }

    public void setServerFacade(ServerFacade server) {
        this.server = server;
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
                default -> new CommandResult(false, "Unknown command, type \"help\" to see more commands.", false, false);
            };
        } catch (Exception e) {
            return new CommandResult(false, "Error processing command: " + e.getMessage(), false, false);
        }
    }

    private CommandResult redraw() {
        if (game == null) {
            return new CommandResult(false, "No game to redraw yet.", false, false);
        }

        ChessBoardPrinter printer = new ChessBoardPrinter();
        printer.print(game.getBoard(), perspective);

        return new CommandResult(true, "", false, false);
    }

    private CommandResult leave() {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            server.sendGameCommand(command);
            return new CommandResult(true, "You have left the game.", false, true);
        } catch (Exception e) {
            return new CommandResult(false, "Error leaving game: " + e.getMessage(), false, false);
        }
    }

    public CommandResult move(String[] params) throws Exception {
        if (params.length != 2) {
            return new CommandResult(false, "Usage: move <startPosition> <endPosition>", false, false);
        }

        try {
            ChessPosition start = translateToChessPosition(params[0]);
            ChessPosition end = translateToChessPosition(params[1]);
            ChessMove move = new ChessMove(start, end, null);

            MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
            server.sendGameCommand(command);

            return new CommandResult(true, "Move sent. Waiting for server validation.", false, false);
        } catch (Exception e) {
            return new CommandResult(false, "Invalid move: " + e.getMessage(), false, false);
        }
    }

    private CommandResult resign() {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            server.sendGameCommand(command);
            return new CommandResult(true, "You have resigned.", false, true);
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
                return new CommandResult(true, "No valid moves from " + params[0] + " to " + params[1], false, false);
            } else {
                ChessBoard board = game.getBoard();

                ChessBoardPrinter printer = new ChessBoardPrinter();
                printer.print(board, perspective, validMoves);
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

    @Override
    public void notify(LoadGameMessage message) {
        this.game = message.getGame().game();
        ChessBoardPrinter printer = new ChessBoardPrinter();
        printer.print(game.getBoard(), perspective);
    }

    @Override
    public void notify(NotificationMessage message) {
        System.out.println(">> " + message.getMessage());
    }

    @Override
    public void notify(ErrorMessage message) {
        System.err.println("Error: " + message.getErrorMessage());
    }
}
