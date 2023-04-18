package me.corxl.ChessServer.game.players;

import me.corxl.ChessServer.Server;
import me.corxl.ChessServer.game.board.Board;
import me.corxl.ChessServer.game.pieces.Piece;
import me.corxl.ChessServer.game.pieces.TeamColor;
import me.corxl.ChessServer.game.spaces.BoardLocation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class PlayerThread extends Thread {

    private Socket socket, updateClient;
    private String playerKey;
    private String lobbyKey;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private Server server;
    private Board board;
    public static int threadCount = 0;
    private final int count;

    public PlayerThread(Socket socket, Server server) throws IOException {
        this.count = threadCount++;
        UUID id = UUID.randomUUID();
        while (Server.getPlayerUUIDs().containsKey(id))
            id = UUID.randomUUID();
        System.out.println("New player with id: " + id);
        this.socket = socket;
        this.playerKey = id.toString();
        this.server = server;
        this.oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(playerKey);


    }

    public void setUpdateClient(Socket updateClient) {
        this.updateClient = updateClient;
    }


    @Override
    public void run() {
        while (true) {
            try {
                oin = new ObjectInputStream(socket.getInputStream());
                Object[] data = (Object[]) oin.readObject();
                String requestType = (String) data[0];
//                System.out.println("This playerkey==" + this.playerKey);
//                System.out.println("Input key==" + playerKey);
                if (requestType.equals("createLobby")) {
                    createLobby();
                } else if (requestType.equals("joinLobby")) {
                    String lobbyCode = (String) data[2];
                    joinLobby(lobbyCode);
                } else if (requestType.equals("getPossibleMoves")) {
                    String code = (String) data[2];
                    int[] pos = (int[]) data[3];
                    System.out.println("UUID: " + playerKey + " code: " + code);
                    Server.getLobbies().forEach((k, v) -> System.out.println(k + " | " + code + " -=- " + v));
                    System.out.println(Server.getLobbies().get(code));
                    this.getPossibleMoves(playerKey, pos[0], pos[1]);
                } else if (requestType.equals("requestMove")) {
                    requestMove(data);
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
                e.printStackTrace();
               break;
            }
        }
    }

    private void requestMove(Object[] data) throws IOException {
        int[] newLocs = (int[]) data[1];
        int[] oldLocs = (int[]) data[2];
        BoardLocation newLoc = new BoardLocation(newLocs[0], newLocs[1]);
        BoardLocation oldLoc = new BoardLocation(oldLocs[0], oldLocs[1]);

        Piece p = this.board.getPieceByPosition(oldLoc);
        Piece targetPiece = this.board.getPieceByPosition(newLoc);
                oout = new ObjectOutputStream(socket.getOutputStream());
        if (p == null) {
            oout.writeObject(null);
            return;
        }
        int newX = newLoc.getX();
        int newY = newLoc.getY();
        if (newX > 8 || newX < 0 || newY > 8 || newY < 0) {
            oout.writeObject(null);
            return;
        }

        if (!p.getPossibleMoves(false)[newX][newY]) {
            oout.writeObject(null);
            return;
        }
        this.board.setPiece(p, oldLoc, newLoc);
        oout.writeObject(p.getPieceType().getKey());
    }

    private void getPossibleMoves(String id, int x, int y) throws IOException {
//        System.out.println("Input id: " + id);
//        String lobbycode = Server.getPlayerUUIDs().get(id);
//        System.out.println("THISSIHSIHS: " + this.playerKey + " | id: " + id);
//        Server.getPlayerUUIDs().forEach((k, v)->{
//            System.out.println(k + " | " + v + " :: " + id);
//        });
//        System.out.println(lobbycode);

        this.board.getPlayers().forEach((k, v) -> {
            System.out.println(k + " | " + v.getColor());
        });

        boolean[][] moveSpaces = new boolean[8][8];
        Piece p = this.board.getSpaces()[x][y].getPiece();

        oout = new ObjectOutputStream(socket.getOutputStream());
        if (p==null || !this.board.isAPlayer(id) || this.board.getColorByPlayer(this.playerKey)!=p.getColor() || this.board.getTurn()!=p.getColor()) {
            oout.writeObject(moveSpaces);
            System.out.println("invalid request.");
            return;
        }
        oout.writeObject(p.getPossibleMoves(false));
    }

    private void createLobby() throws IOException {
        HashMap<String, Board> lobbies = Server.getLobbies();
        HashMap<String, String> playerUUIDs = Server.getPlayerUUIDs();
        // Destroys lobby if a user already has a lobby created.
        if (lobbies.get(Server.getPlayerUUIDs().get(playerKey)) != null) {
            lobbies.remove(playerUUIDs.get(playerKey));
        }
        String lobbyId;
        do {
            lobbyId = UUID.randomUUID().toString().substring(0, 7);
        } while (lobbies.containsKey(lobbyId));
        playerUUIDs.put(playerKey, lobbyId);
        Board board = Server.createBoard(lobbyId);
        lobbies.put(lobbyId, board);
        this.board = board;
        this.lobbyKey = lobbyId;
        System.out.println("Lobby created with the ID of: " + lobbyKey + " by: " + this.playerKey);
        lobbies.get(lobbyKey).addPlayer(playerKey, new Player(TeamColor.WHITE, "White", this));
        oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(lobbyKey);
    }

    private void joinLobby(String lobbyKey) throws IOException, InterruptedException {
        HashMap<String, Board> lobbies = Server.getLobbies();
        HashMap<String, String> playerUUIDs = Server.getPlayerUUIDs();
        if (lobbies.containsKey(lobbyKey)) {
            if (lobbies.get(lobbyKey).isAPlayer(this.playerKey)) {
                oout = new ObjectOutputStream(socket.getOutputStream());
                oout.writeObject("Already in lobby.");
                return;
            }
            this.lobbyKey = lobbyKey;
            System.out.println("Key??: " + lobbyKey);
            this.board = lobbies.get(this.lobbyKey);

            //this.board.swapTurn();

            System.out.println("\n\n");
            this.board.getPlayers().forEach((k, v) -> {
                System.out.println("ID: " + k + "\nColor: " + v);
            });
            System.out.println("\n\n");

            System.out.println(this.playerKey + " has joined the lobby with the code: " + this.lobbyKey);
            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject("Joining game...");
            System.out.println("Updating to 1st client.");
//            Thread.sleep(100);
            System.out.println("-----------");
            System.out.println("--------------");
            this.board.addPlayer(this.playerKey, new Player(TeamColor.BLACK, "Black", this));
            System.out.println("Updating plyers with key: " + lobbyKey);
            this.board.updatePlayers(lobbyKey);

            System.out.println("\n\n");
            this.board.getPlayers().forEach((k, v) -> {
                System.out.println("ID: " + k + "\nColor: " + v);
            });
            System.out.println("\n\n");

        } else {
            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.writeObject("Lobby code not found.");
        }
    }

    public void updateGame(Integer[][][] layout, int teamColor) throws IOException {
        ObjectOutputStream uoout = new ObjectOutputStream(updateClient.getOutputStream());
        Object[] data = new Object[]{"updateBoard", layout, teamColor};
        uoout.writeObject(data);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setLobbyKey(String key) {
        System.out.println("Key: " + key + " set to " + this.playerKey + " on thread: " + Thread.currentThread().getPriority());
        this.lobbyKey = key;
        Server.getPlayerUUIDs().put(this.playerKey, key);
    }

}
