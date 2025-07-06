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
        Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(myBoard, startPosition);
        ChessBoard originalBoard = myBoard;

        for (ChessMove move : pieceMoves) {
            ChessBoard newBoard = myBoard.copyBoard();
            movePiece(newBoard, move);
            myBoard = newBoard;
            if (!isInCheck(currentPiece.getTeamColor())) {
                valid.add(move);
            }
            myBoard = originalBoard;
        }
        return valid;
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
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("Invalid move: Null piece.");
        }
        if (piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Invalid move: It is not this color's turn.");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move: This piece cannot move to this space.");
        }
        movePiece(myBoard, move);
        if (currentTeam == TeamColor.WHITE) {
            currentTeam = TeamColor.BLACK;
        } else {
            currentTeam = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (myBoard.getPiece(new ChessPosition(row, col)) != null) {
                    ChessPiece piece = myBoard.getPiece(new ChessPosition(row, col));
                    System.out.print("is checking King, piece: " + piece.getTeamColor() + piece.getPieceType() + "\n");
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(myBoard, new ChessPosition(row, col));
                    for (ChessMove move : possibleMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row=1; row <= 8; row++) {
            for (int col=1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = myBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    System.out.print("King position: " + position.getRow() + ", " + position.getColumn() + "\n");
                    return position;
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
        boolean hasValidMoves = false;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = myBoard.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(currentPosition).isEmpty()) {
                        hasValidMoves = true;
                        break;
                    }
                }
            }
        }
        return !hasValidMoves && !isInCheckmate(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
