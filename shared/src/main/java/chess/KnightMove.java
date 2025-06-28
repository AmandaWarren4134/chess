package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMove {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        int [][] knightMovements = {{-1,2}, {-2, 1}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};

        for (int[] knightMovement : knightMovements) {
            int nextRow = myPosition.getRow() + knightMovement[0];
            int nextColumn = myPosition.getColumn() + knightMovement[1];
            ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);
            if (nextPosition.isOnBoard()) {
                ChessPiece targetPiece = board.getPiece(nextPosition);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }



        return moves;
    }
}
