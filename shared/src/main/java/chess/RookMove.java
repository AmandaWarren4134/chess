package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMove {
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int up = 8 - myPosition.getRow();
        int down = myPosition.getRow();
        int left = 8 - myPosition.getColumn();
        int right = myPosition.getColumn();
        for (int i = ) {

        }

        return moves;
    }
}
