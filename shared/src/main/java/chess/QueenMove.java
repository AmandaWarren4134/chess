package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMove {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        addDirection(1, 1, board, myPosition, piece, moves);
        addDirection(1, -1, board, myPosition, piece, moves);
        addDirection(-1, 1, board, myPosition, piece, moves);
        addDirection(-1, -1, board, myPosition, piece, moves);
        addDirection(1, 0, board, myPosition, piece, moves);
        addDirection(0, 1, board, myPosition, piece, moves);
        addDirection(-1, 0, board, myPosition, piece, moves);
        addDirection(0, -1, board, myPosition, piece, moves);
        return moves;
    }

    /* Start moving in a direction, check if it is on the board, if it is blocked by a piece of the same color,
     * or if it is a piece that can be captured */
    private static void addDirection(int row, int col, ChessBoard board, ChessPosition myPosition, ChessPiece piece, Collection<ChessMove> moves) {
        int nextRow = myPosition.getRow();
        int nextColumn = myPosition.getColumn();

        while (true) {
            nextRow += row;
            nextColumn += col;
            ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);

            if (!nextPosition.isOnBoard()) {
                break;
            }

            ChessPiece targetPiece = board.getPiece(nextPosition);

            if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                ChessMove possibleMove = new ChessMove(myPosition, nextPosition);
                moves.add(possibleMove);
                if (targetPiece != null) {
                    break;
                }
            } else { break;}
        }
    }
}
