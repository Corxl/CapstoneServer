package me.corxl.ChessServer.game.players;

import me.corxl.ChessServer.Server;
import me.corxl.ChessServer.game.board.Board;
import me.corxl.ChessServer.game.pieces.TeamColor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class PlayerThread extends Thread {

    private Socket socket, updateClient;
    private UUID playerKey;
    private String lobbyKey;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private Server server;
    private Board board;

    public PlayerThread(Socket socket, UUID playerKey, Server server) throws IOException {
        this.socket = socket;
        this.playerKey = playerKey;
        this.server = server;
        this.oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(playerKey);
        this.updateClient = server.getServer().accept();


    }

    @Override
    public void run() {
        while (true) {
            try {
                oin = new ObjectInputStream(socket.getInputStream());
                Object[] data = (Object[]) oin.readObject();
                String requestType = (String) data[0];
                UUID playerKey = (UUID) data[1];
                if (requestType.equals("createLobby")) {
                    createLobby(playerKey);
                } else if (requestType.equals("joinLobby")) {
                    String lobbyCode = (String) data[2];
                    joinLobby(playerKey, lobbyCode);
                }
//                if (requestType.equals("getPossibleMoves")) {
//                    requestPossibleMoves(data);
//                } else if (requestType.equals("getDefaultSpaces")) {
//                    getDefaultSpaces();
//                } else if (requestType.equals("requestMove")) {
//                    requestSetPiece(data);
//                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.out.println("Player with id: " + playerKey + " has disconnected.");
               break;
            }
        }
    }

    private void createLobby(UUID playerKey) throws IOException {
        HashMap<String, Board> lobbies = Server.getLobbies();
        HashMap<UUID, String> playerUUIDs = Server.getPlayerUUIDs();
        // Destroys lobby if a user already has a lobby created.
        if (lobbies.get(Server.getPlayerUUIDs().get(playerKey)) != null) {
            lobbies.remove(playerUUIDs.get(playerKey));
        }
        String lobbyId;
        do {
            lobbyId = UUID.randomUUID().toString().substring(0, 7);
        } while (lobbies.containsKey(lobbyId));
        playerUUIDs.put(playerKey, lobbyId);
        lobbies.put(lobbyId, Server.createBoard(lobbyId));
        this.lobbyKey = lobbyId;
        System.out.println("Lobby created with the ID of: " + lobbyKey);
        System.out.println(Server.getLobbysSize());
        lobbies.get(lobbyKey).addPlayer(playerKey, new Player(TeamColor.WHITE, "White", this));
        oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(lobbyKey);
    }

    private void joinLobby(UUID playerID, String lobbyKey) throws IOException, InterruptedException {
        HashMap<String, Board> lobbies = Server.getLobbies();
        HashMap<UUID, String> playerUUIDs = Server.getPlayerUUIDs();
        if (lobbies.containsKey(lobbyKey)) {
            if (lobbies.get(lobbyKey).isAPlayer(playerID)) {
                oout = new ObjectOutputStream(socket.getOutputStream());
                oout.writeObject("Already in lobby.");
                return;
            }
            this.board = lobbies.get(lobbyKey);

            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject("Joining game...");
            System.out.println("Updating to 1st client.");
//            Thread.sleep(100);
            this.board.addPlayer(playerID, new Player(TeamColor.BLACK, "Black", this));

            this.board.updatePlayers();

        } else {
            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject("Lobby code not found.");
        }
    }

    public void updateGame(Integer[][][] layout) throws IOException {
        ObjectOutputStream uoout = new ObjectOutputStream(updateClient.getOutputStream());
        Object[] data = new Object[]{"updateBoard", layout};
        uoout.writeObject(data);
    }

}
