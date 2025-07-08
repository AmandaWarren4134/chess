package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMove {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int nextRow = myPosition.getRow() + i;
                int nextColumn = myPosition.getColumn() + j;
                ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);

                if (nextPosition.isOnBoard()) {
                    ChessPiece targetPiece = board.getPiece(nextPosition);
                    if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, nextPosition));
                    }
                }
            }
        }

        return moves;
    }
}
