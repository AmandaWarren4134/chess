package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMove extends AbstractMove {

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


}
