package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMove {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int nextRow = myPosition.getRow() + i;
                int nextColumn = myPosition.getColumn() + j;
                ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);
                ChessMove possibleMove = new ChessMove(myPosition, nextPosition);
                if (possibleMove.isOnBoard()) {
                    moves.add(possibleMove);
                }
            }
        }
        return moves;
    }
}
