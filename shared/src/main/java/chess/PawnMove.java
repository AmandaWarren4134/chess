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
        int nextRow = currentRow + direction;
        if (isOnBoard(nextRow, currentCol)) {
            ChessPosition oneStepForward = new ChessPosition(nextRow, currentCol);
            if (board.getPiece(oneStepForward) == null) {
                addMove(moves, myPosition, oneStepForward, nextRow);

                // Forward two squares
                if (currentRow == startRow) {
                    int twoStepRow = currentRow + 2 * direction;
                    if (isOnBoard(twoStepRow, currentCol)) {
                        ChessPosition twoStepsForward = new ChessPosition(twoStepRow, currentCol);
                        if (board.getPiece(twoStepsForward) == null) {
                            moves.add(new ChessMove(myPosition, twoStepsForward));
                        }
                    }
                }
            }
        }

        // En Passant
        if (lastMove != null) {
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (lastMovedPiece != null && lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN && lastMovedPiece.getTeamColor() != piece.getTeamColor()) {
                int lastStartRow = lastMove.getStartPosition().getRow();
                int lastEndRow = lastMove.getEndPosition().getRow();
                int lastEndCol = lastMove.getEndPosition().getColumn();

                if (Math.abs(lastEndRow - lastStartRow) == 2) {
                    int enPassantRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 5 : 4;
                    if (Math.abs(lastEndCol - myPosition.getColumn()) == 1 && myPosition.getRow() == enPassantRow) {
                        int enpassant = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
                        int captureRow = myPosition.getRow() + enpassant;
                        ChessPosition enPassantCapture = new ChessPosition(captureRow, lastEndCol);

                        if (board.getPiece(enPassantCapture) == null) {
                            moves.add(new ChessMove(myPosition, enPassantCapture));
                        }
                    }
                }
            }
        }

        // Captures
        for (int dc : new int[]{-1, 1}) {
            int captureCol = currentCol + dc;
            int captureRow = currentRow + direction;
            if (isOnBoard(captureRow, captureCol)) {
                ChessPosition enemyPos = new ChessPosition(captureRow, captureCol);
                ChessPiece target = board.getPiece(enemyPos);
                if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                    addMove(moves, myPosition, enemyPos, captureRow);
                }
            }
        }

        return moves;
    }

    private static void addMove(Collection<ChessMove> moves, ChessPosition from, ChessPosition to, int toRow) {
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
}
