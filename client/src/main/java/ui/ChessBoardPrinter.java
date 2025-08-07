package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String [] COLUMN_LABELS = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String [] ROW_LABELS = {"1", "2", "3", "4", "5", "6", "7", "8"};

    // Constructor for printing normal board
    public void print(ChessBoard board, ChessGame.TeamColor perspective) {
        print(board, perspective, null, null);
    }

    // Constructor for printing board with valid moves highlighted
    public void print(ChessBoard board, ChessGame.TeamColor perspective, Collection<ChessMove> validMoves, ChessPosition highlightPosition) {
        // Set up PrintStream and clear screen
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        // Define rows, columns, and board orientation
        int rowStart, rowEnd, rowStep;
        int colStart, colEnd, colStep;

        if (perspective == WHITE || perspective == null) {
            rowStart = 7; rowEnd = -1; rowStep = -1;
            colStart = 0; colEnd = 8; colStep = 1;
        }
        else {
            rowStart = 0; rowEnd = 8; rowStep = 1;
            colStart = 7; colEnd = -1; colStep = -1;
        }

        // Draw the top column labels
        drawHeaders(out, perspective);

        // Create a set of valid positions from validMoves; if normal board, create empty set
        Set<ChessPosition> validPositions = getValidPositions(validMoves);

        // Print main board
        for (int row = rowStart; row != rowEnd; row += rowStep) {
            // left-side row label
            resetColors(out);
            setBlack(out);
            out.print(" " + ROW_LABELS[row] + " ");

                // Draw each square in the row
                for (int col = colStart; col != colEnd; col += colStep) {
                    ChessPiece currentPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
                    setColors(out, row, col, validPositions, highlightPosition);
                    String symbol = getPieceSymbol(currentPiece);
                    out.print(symbol);
                }

                // Right-side row label
                resetColors(out);
                setBlack(out);
                out.print(" " + ROW_LABELS[row] + " ");
                resetColors(out);
                out.println();
        }
        // Draw bottom column labels
        drawHeaders(out, perspective);
    }

    private Set<ChessPosition> getValidPositions(Collection<ChessMove> validMoves) {
        if (validMoves != null) {
            return validMoves.stream()
                    .map(ChessMove::getEndPosition)
                    .collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    private void setColors(PrintStream out, int row, int col, Set<ChessPosition> validPositions, ChessPosition highlightPosition) {
        // Determine the background color
        boolean isDark = (row + col) % 2 == 0;
        ChessPosition pos = new ChessPosition(row + 1, col + 1);
        if (pos.equals(highlightPosition)) {
            setYellowHighlight(out);
        } else if (validPositions.contains(pos) && isDark) {
            setDarkHighlight(out);
        } else if (validPositions.contains(pos)) {
            setLightHighlight(out);
        } else if (isDark) {
            setDarkSquare(out);
        } else {
            setLightSquare(out);
        }
    }

    private static String[] reverse(String[] array) {
        String[] reversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor perspective) {
        setBlack(out);

        String[] columns = (perspective == WHITE || perspective == null) ? COLUMN_LABELS : reverse(COLUMN_LABELS);

        out.print("   ");

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
            drawHeader(out, columns[col]);
        }

        resetColors(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
        out.print("   ");
    }

    private static void printHeaderText(PrintStream out, String player) {
        setBlack(out);
        out.print(player);
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {return EMPTY;}

        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        };
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private static void setLightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkSquare(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkHighlight(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setLightHighlight(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setYellowHighlight(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void resetColors(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

}
