package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_WIDTH = 3;
    private static final int SQUARE_HEIGHT = 3;
    private static final String [] COLUMN_LABELS = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String [] ROW_LABELS = {"1", "2", "3", "4", "5", "6", "7", "8"};

    public void print(ChessBoard board, ChessGame.TeamColor perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        int rowStart, rowEnd, rowStep;
        int colStart, colEnd, colStep;

        if (perspective == WHITE) {
            rowStart = 7; rowEnd = -1; rowStep = -1;
            colStart = 0; colEnd = 7; colStep = 1;
        }
        else {
            rowStart = 0; rowEnd = 7; rowStep = 1;
            colStart = 7; colEnd = -1; colStep = -1;
        }

        drawHeaders(out, perspective);

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            for (int squareRow = 0; squareRow < SQUARE_HEIGHT; squareRow++) {
                out.print(EMPTY);
                out.print(ROW_LABELS[row]);
                for (int col = colStart; col != colEnd; col += colStep) {
                    if ((row + col) % 2 == 0) {
                        setLightSquare(out);
                    } else {
                        setDarkSquare(out);
                    }
                    if (squareRow == SQUARE_HEIGHT / 2) {
                        ChessPiece currentPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
                        out.print(getPieceSymbol(currentPiece));
                    } else {
                        out.print(EMPTY);
                    }
                }
                out.print(EMPTY);
                out.print(ROW_LABELS[row]);
                resetColors(out);
                out.println();
            }
            out.println();
        }

        drawHeaders(out, perspective);

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

        out.print(EMPTY);
        String[] columns = (perspective == WHITE) ? COLUMN_LABELS : reverse(COLUMN_LABELS);
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
            drawHeader(out, columns[col]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_WIDTH / 2;
        int suffixLength = SQUARE_WIDTH - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);

        out.print(player);

        setBlack(out);
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EMPTY;

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
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setLightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkSquare(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void resetColors(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

}
