package chess;

import java.util.Collection;

public abstract class AbstractMove {

    /* Start moving in a direction, check if it is on the board, if it is blocked by a piece of the same color,
     * or if it is a piece that can be captured */
    protected static void addDirection(int rowDelta, int colDelta, ChessBoard board,
                                       ChessPosition myPosition, ChessPiece piece,
                                       Collection<ChessMove> moves) {
        int nextRow = myPosition.getRow();
        int nextColumn = myPosition.getColumn();

        while (true) {
            nextRow += rowDelta;
            nextColumn += colDelta;
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
            } else {
                break;
            }
        }
    }

    protected static void setOfMovements(int[][] setMovements, ChessBoard board,
                                         ChessPosition myPosition, ChessPiece piece,
                                         Collection<ChessMove> moves) {
        for (int[] movement : setMovements) {
            int nextRow = myPosition.getRow() + movement[0];
            int nextColumn = myPosition.getColumn() + movement[1];
            ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);
            if (nextPosition.isOnBoard()) {
                ChessPiece targetPiece = board.getPiece(nextPosition);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }
    }
}
