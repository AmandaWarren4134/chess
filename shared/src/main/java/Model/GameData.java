package Model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData update(String username, ChessGame.TeamColor playerColor, ChessGame game) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return new GameData(gameID, username, (blackUsername), (gameName), (game));
        }
        else if (playerColor == ChessGame.TeamColor.BLACK) {
            return new GameData(gameID, (whiteUsername), username, (gameName), (game));
        } else {
            throw new IllegalArgumentException("Invalid player color");
        }
    }
}
