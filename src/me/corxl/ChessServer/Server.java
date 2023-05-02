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
    private final static HashMap<String, String> playerUUIDs = new HashMap<>();
    private final static HashMap<String, Board> lobbies = new HashMap<>();
    public Server() throws IOException {
        this.server = new ServerSocket(port);
        int threadCount = 0;
        while (true) {
            Socket socketInput = server.accept();
            PlayerThread newPlayer = new PlayerThread(socketInput, this);
            Socket socketOutput = server.accept();
            newPlayer.setUpdateClient(socketOutput);
            newPlayer.start();
        }
    }

    public static HashMap<String, String> getPlayerUUIDs() {
        return playerUUIDs;
    }
    public static HashMap<String, Board> getLobbies() {
        return lobbies;
    }

    public static Board createBoard(String lobbyKey) {
        Board b = new Board(lobbyKey);

        return b;
    }

    public static void clearLobby(String key) {
        lobbies.remove(key);
    }

}
