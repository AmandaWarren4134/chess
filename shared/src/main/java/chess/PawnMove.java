package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PawnMove {
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, ChessMove lastMove) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        // Move forward one square
        ChessPosition oneStepForward = new ChessPosition(currentRow + direction, currentCol);
        if (canMoveTo(board, oneStepForward)) {
            addMove(moves, myPosition, oneStepForward);

            // Forward two squares
            if (currentRow == startRow) {
                ChessPosition twoStepsForward = new ChessPosition(currentRow + 2 * direction, currentCol);
                int twoStepRow = currentRow + 2 * direction;
                if (canMoveTo(board, twoStepsForward)) {
                    moves.add(new ChessMove(myPosition, twoStepsForward));
                }
            }
        }

        // En Passant
        if (lastMove != null) {
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (isEnPassantPossible(piece, myPosition, lastMove, lastMovedPiece)) {
                int captureRow = myPosition.getRow() + direction;
                int captureCol = lastMove.getEndPosition().getColumn();
                ChessPosition enPassantCapture = new ChessPosition(captureRow, captureCol);

                if (board.getPiece(enPassantCapture) == null) {
                    moves.add(new ChessMove(myPosition, enPassantCapture));
                }
            }
        }

        // Captures
        for (int dc : new int[]{-1, 1}) {
            ChessPosition capturePos = new ChessPosition(currentRow + direction, currentCol + dc);
            if (isOnBoard(capturePos.getRow(), capturePos.getColumn()) && isEnemyPiece(board, capturePos, piece)) {
                addMove(moves, myPosition, capturePos);
            }
        }

        return moves;
    }

    private static void addMove(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        int toRow = to.getRow();
        if (toRow == 1 || toRow == 8) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to));
        }
    }

    private static boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private static boolean isEnemyPiece(ChessBoard board, ChessPosition pos, ChessPiece piece) {
        ChessPiece target = board.getPiece(pos);
        return target != null && target.getTeamColor() != piece.getTeamColor();
    }

    private static boolean canMoveTo(ChessBoard board, ChessPosition pos) {
        return isOnBoard(pos.getRow(), pos.getColumn()) && board.getPiece(pos) == null;
    }

    private static boolean isEnPassantPossible(ChessPiece piece, ChessPosition myPosition, ChessMove lastMove, ChessPiece lastMovedPiece) {
        if (lastMovedPiece == null) {
            return false;
        }
        if (lastMovedPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return false;
        }
        if (lastMovedPiece.getTeamColor() == piece.getTeamColor()) {
            return false;
        }

        int lastStartRow = lastMove.getStartPosition().getRow();
        int lastEndRow = lastMove.getEndPosition().getRow();
        int lastEndCol = lastMove.getEndPosition().getColumn();

        if (Math.abs(lastEndRow - lastStartRow) != 2) {
            return false;
        }

        int enPassantRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 5 : 4;
        if (myPosition.getRow() != enPassantRow) {
            return false;
        }

        return Math.abs(lastEndCol - myPosition.getColumn()) == 1;
    }
}
