package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam = TeamColor.WHITE;
    private ChessBoard myBoard = new ChessBoard();


    public ChessGame() {
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which team's turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> valid = new HashSet<>();
        ChessPiece currentPiece = myBoard.getPiece(startPosition);
        if (currentPiece == null) {
            return null;
        }
        Collection <ChessMove> pieceMoves = currentPiece.pieceMoves(myBoard, startPosition);
        for (ChessMove move : pieceMoves) {
            ChessBoard newBoard = myBoard.copyBoard();
            movePiece(newBoard, move);
            if (isInCheck(currentTeam)){
                break;
            }
            else {
                valid.add(move);
            }
        }
        return pieceMoves;
    }

    private void movePiece(ChessBoard board, ChessMove move) {
        ChessPosition from = move.getStartPosition();
        ChessPosition to = move.getEndPosition();
        ChessPiece piece = board.getPiece(from);
        board.addPiece(to, piece);
        board.removePiece(from);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, myBoard);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition to = move.getEndPosition();
        ChessPosition from = move.getStartPosition();
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new NullPointerException();
        }
        if (piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Invalid move: It is not this color's turn.");
        }

        ChessMove nextMove = new ChessMove(from, to);
        if (!)
        if (move.getPromotionPiece() != null) {

        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean check = false;
        ChessPosition kingPosition = getKingPosition(teamColor);
        for (int row=1; row <= 8; row++) {
            for (int col=1; col <= 8; col++) {
                if (myBoard.getPiece(new ChessPosition (row, col)) != null) {
                    ChessPiece piece = myBoard.getPiece(new ChessPosition (row, col));
                    Collection <ChessMove> possibleMoves = piece.pieceMoves(myBoard, new ChessPosition(row, col));
                    for (ChessMove move: possibleMoves) {
                        if (move.getEndPosition() == kingPosition) {
                            check = true;
                        }
                    }
                }
            }
        }
        return check;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row=1; row <= 8; row++) {
            for (int col=1; col <= 8; col++) {
                ChessPiece piece = myBoard.getPiece(new ChessPosition(row, col));
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(row, col);
                }
            }
        }
        throw new RuntimeException("The King is not on the board.");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
