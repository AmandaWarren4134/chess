package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMove extends AbstractMove {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int [][] kingMovements = {{-1,-1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        setOfMovements(kingMovements, board, myPosition, piece, moves);

        return moves;
    }
}
