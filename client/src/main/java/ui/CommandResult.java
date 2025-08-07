package ui;

import chess.ChessGame;

public class CommandResult {
    public final boolean success;
    public final String message;
    public final boolean goForward;
    public final boolean quit;
    public final String authToken;
    public final String username;
    public final Integer gameID;
    public final ChessGame.TeamColor perspective;

    public CommandResult(
            boolean success,
            String message,
            boolean goToNextLoop,
            boolean quit
    ) {
        this(success, message, goToNextLoop, quit, null, null, null, null);
    }

    public CommandResult(boolean success, String message, boolean goToNextLoop, boolean quit, String authToken, String username, Integer gameID, ChessGame.TeamColor perspective) {
        this.success = success;
        this.message = message;
        this.goForward = goToNextLoop;
        this.quit = quit;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.perspective = perspective;
    }

    public String getMessage() {return message;}
    public boolean isQuit() {return quit;}
    public String getAuthToken() {return authToken;}
    public String getUsername() {return username;}
    public Integer getGameID() {return gameID;}
    public ChessGame.TeamColor getPerspective() {return perspective;}
}
