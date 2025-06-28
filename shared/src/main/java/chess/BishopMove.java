package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMove {
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        for (int i = 1; i < 8; i++) {
            ChessPosition upRight = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            ChessMove possibleMove = new ChessMove(myPosition, upRight);
            if (possibleMove.isOnBoard()) {
                moves.add(possibleMove);
            }
            ChessPosition downRight = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            ChessMove possibleMove = ChessMove(myPosition, downRight);
            if (possibleMove.isOnBoard()) {
                moves.add(possibleMove);
            }
            ChessPosition upLeft = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            ChessMove possibleMove = new ChessMove(myPosition, upRight);
            if (possibleMove.isOnBoard()) {
                moves.add(possibleMove);
            }
            ChessPosition upRight = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            ChessMove possibleMove = new ChessMove(myPosition, upRight);
            if (possibleMove.isOnBoard()) {
                moves.add(possibleMove);
            }
        }

        return moves;
    }
}
