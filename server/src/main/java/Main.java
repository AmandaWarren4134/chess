import chess.*;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        run(8080);
        //stop();
    }
    public static void run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        //Register your endpoints and handle exceptions here.
        Spark.get("/", (req, res) -> {

            return "Welcome to the Chess Server!";
        });

        Spark.awaitInitialization();
    }

    public static void stop() {
        Spark.stop();
    }
}