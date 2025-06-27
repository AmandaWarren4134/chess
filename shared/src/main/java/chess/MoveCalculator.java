package chess;

import java.util.Collection;
import java.util.HashSet;

public class MoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        } else {throw new RuntimeException("Not implemented");}
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
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

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();


        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
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

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int up = 8 - myPosition.getRow();
        int down = myPosition.getRow();
        int left = 8 - myPosition.getColumn();
        int right = myPosition.getColumn();
        for (int i = ) {

        }

        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        return moves;
    }
}
