package me.corxl.ChessServer;

import me.corxl.ChessServer.game.board.Board;
import me.corxl.ChessServer.game.players.PlayerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Server {
    private Socket socket;
    private ServerSocket server;
    private static int port = 4909;
    private final static HashMap<UUID, String> playerUUIDs = new HashMap<>();
    private final static HashMap<String, Board> lobbies = new HashMap<>();
    public Server() throws IOException {
        this.server = new ServerSocket(port);
        while (true) {
            socket = server.accept();
            UUID id = UUID.randomUUID();
            while (playerUUIDs.containsKey(id))
                id = UUID.randomUUID();
            playerUUIDs.put(id, "");
            System.out.println("New player with id: " + id);
            new PlayerThread(socket, id, this).start();
        }
    }

    public static HashMap<UUID, String> getPlayerUUIDs() {
        return playerUUIDs;
    }
    public static HashMap<String, Board> getLobbies() {
        return lobbies;
    }

    public static Board createBoard(String lobbyKey) {
        Board b = new Board(lobbyKey);

        return b;
    }

    public static void sendBoardUpdate(String lobbyID) {

    }

    public static int getLobbysSize() {
        return lobbies.size();
    }

    public ServerSocket getServer() {
        return this.server;
    }

}
