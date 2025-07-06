package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessBoard() {
        boardArray = new ChessPiece [8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardArray[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Removes a chess piece from the chessboard and replaces it with null
     *
     * @param position where to add the piece to
     */
    public void removePiece(ChessPosition position) {
        boardArray[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardArray[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Creates a deep copy of the board
     * (Returns the copy)
     */

    public ChessBoard copyBoard() {
        ChessBoard clone = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece originalPiece = this.boardArray[i][j];
                if (originalPiece != null) {
                    ChessPiece clonePiece = new ChessPiece(originalPiece.getTeamColor(), originalPiece.getPieceType());
                    ChessPosition clonePosition = new ChessPosition(i+1, j+1);
                    clone.addPiece(clonePosition, clonePiece);
                }
            }
        }
        return clone;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(boardArray, that.boardArray);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardArray);
    }

    private ChessPiece [][] boardArray;

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardArray = new ChessPiece [8][8];
        setUpPawns(2, ChessGame.TeamColor.WHITE);
        setUpRank(1, ChessGame.TeamColor.WHITE);

        setUpPawns(7, ChessGame.TeamColor.BLACK);
        setUpRank(8, ChessGame.TeamColor.BLACK);
    }

    private void setUpPawns(int row, ChessGame.TeamColor color) {
        for (int col=1; col < 9; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    private void setUpRank(int row, ChessGame.TeamColor color) {
        ChessPiece.PieceType[] order = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        for (int col = 1; col < 9; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, order[col -1]));
        }
    }
}
