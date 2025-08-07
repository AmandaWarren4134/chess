import chess.*;
import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        int port = 8080;

        int actualPort = server.run(port);
        System.out.print("Server is running on port: " + actualPort);
    }
}