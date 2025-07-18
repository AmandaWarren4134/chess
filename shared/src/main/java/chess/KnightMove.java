package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMove extends AbstractMove {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int [][] knightMovements = {{-1,2}, {-2, 1}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
        setOfMovements(knightMovements, board, myPosition, piece, moves);

        return moves;
    }
}
