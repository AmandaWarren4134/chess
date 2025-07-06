package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PawnMove {
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        List<int[]> pawnMovements = new ArrayList<>();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition oneInFront = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(oneInFront) == null) {
                pawnMovements.add(new int[]{1,0});
            }
            ChessPosition twoInFront = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            if (myPosition.getRow() == 2 && board.getPiece(oneInFront) == null && board.getPiece(twoInFront) == null) {
                pawnMovements.add(new int[]{2, 0});
            }

            ChessPosition enemyLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1);
            if (enemyLeft.isOnBoard() && myPosition.getColumn() > 1) {
                ChessPiece leftTarget = board.getPiece(enemyLeft);
                if (leftTarget != null && leftTarget.getTeamColor() != piece.getTeamColor()) {
                    pawnMovements.add(new int[]{1, -1});
                }
            }
            ChessPosition enemyRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() +1);
            if (enemyRight.isOnBoard() && myPosition.getColumn() < 8) {
                ChessPiece rightTarget = board.getPiece(enemyRight);
                if (rightTarget != null && rightTarget.getTeamColor() != piece.getTeamColor()) {
                    pawnMovements.add(new int[]{1, 1});
                }
            }
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            ChessPosition oneInFront = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (board.getPiece(oneInFront) == null) {
                pawnMovements.add(new int[]{-1, 0});
            }
            ChessPosition twoInFront = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
            if (myPosition.getRow() == 7 && board.getPiece(oneInFront) == null && board.getPiece(twoInFront) == null) {
                pawnMovements.add(new int[]{-2, 0});
            }

            ChessPosition enemyLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() -1);
            if (enemyLeft.isOnBoard()) {
                ChessPiece leftTarget = board.getPiece(enemyLeft);
                if (leftTarget != null && leftTarget.getTeamColor() != piece.getTeamColor()) {
                    pawnMovements.add(new int[]{-1, -1});
                }
            }
            ChessPosition enemyRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() +1);
            if (enemyRight.isOnBoard()) {
                ChessPiece rightTarget = board.getPiece(enemyRight);
                if (rightTarget != null && rightTarget.getTeamColor() != piece.getTeamColor()) {
                    pawnMovements.add(new int[]{-1, 1});
                }
            }
        }

        for (int [] pawnMovement : pawnMovements) {
            int nextRow = myPosition.getRow() + pawnMovement[0];
            int nextColumn = myPosition.getColumn() + pawnMovement[1];
            ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);
            if (nextPosition.isOnBoard()) {
                ChessPiece targetPiece = board.getPiece(nextPosition);
                if (nextRow == 8 || nextRow == 1) {
                    if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
                    }
                }
                else if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition));
                }
            }
        }
        return moves;
    }
}
