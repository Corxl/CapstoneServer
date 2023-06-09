package me.corxl.ChessServer.game.board;

import me.corxl.ChessServer.Server;
import me.corxl.ChessServer.game.pieces.Piece;
import me.corxl.ChessServer.game.pieces.PieceType;
import me.corxl.ChessServer.game.pieces.TeamColor;
import me.corxl.ChessServer.game.players.Player;
import me.corxl.ChessServer.game.spaces.BoardLocation;
import me.corxl.ChessServer.game.spaces.Space;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Board {
    private Space[][] spaces;
    private final HashMap<String, Player> players = new HashMap<>();
    private final HashMap<TeamColor, Boolean> isChecked = new HashMap<>();
    private final static HashMap<TeamColor, TeamColor> opposingColors = new HashMap<>();
    private TeamColor turn;
    private String gameKey;
    private static final PieceType[][] defaultPieces = new PieceType[][]{
            {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK},
            {PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN},
            {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.KING, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK}
    };

    public HashMap<String, Player> getPlayers() {
        return this.players;
    }
    public Board(String gameKey) {
        this.gameKey = gameKey;
        opposingColors.put(TeamColor.WHITE, TeamColor.BLACK);
        opposingColors.put(TeamColor.BLACK, TeamColor.WHITE);
        isChecked.put(TeamColor.WHITE, false);
        isChecked.put(TeamColor.BLACK, false);

        PieceType ro = PieceType.ROOK, kn = PieceType.KNIGHT, bi = PieceType.BISHOP, qu = PieceType.QUEEN, ki = PieceType.KING, pa = PieceType.PAWN;
        TeamColor w = TeamColor.WHITE, b = TeamColor.BLACK;
        BoardLayout[][] layout = new BoardLayout[][]{
                {new BoardLayout(ro, b), new BoardLayout(kn, b), new BoardLayout(bi, b), new BoardLayout(qu, b), new BoardLayout(ki, b), new BoardLayout(bi, b), new BoardLayout(kn, b), new BoardLayout(ro, b)},
                {new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b)},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w)},
                {new BoardLayout(ro, w), new BoardLayout(kn, w), new BoardLayout(bi, w), new BoardLayout(ki, w), new BoardLayout(qu, w), new BoardLayout(bi, w), new BoardLayout(kn, w), new BoardLayout(ro, w)}
        };
//        BoardLayout[][] layout = new BoardLayout[][]{
//                {new BoardLayout(ro, b), new BoardLayout(kn, b), new BoardLayout(bi, b), new BoardLayout(qu, b), new BoardLayout(ki, b), null, null, new BoardLayout(ro, b)},
//                {null, null, null, null, null,  new BoardLayout(pa, b), null, null},
//                {new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b), new BoardLayout(pa, b)},
//                {null, null, null, null, null, null, null, null},
//                {null, null, null, null, null, null, null, null},
//                {null, null, null, null, null,null, null, null},
//                {new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w), new BoardLayout(pa, w)},
//                {new BoardLayout(ro, w), null, null, new BoardLayout(ki, w), new BoardLayout(qu, w), new BoardLayout(bi, w), new BoardLayout(kn, w), new BoardLayout(ro, w)}
//        };

        spaces = new Space[8][8];
        turn = TeamColor.WHITE;

        for (int i = 0; i < layout.length; i++) {
            for (int i1 = 0; i1 < layout[i].length; i1++) {
                BoardLayout boardLayout = layout[i][i1];
                BoardLocation loc = new BoardLocation(i, i1);
                spaces[i][i1] = layout[i][i1] == null ? new Space(new BoardLocation(i, i1), this) : new Space(loc, new Piece(boardLayout.getType(), boardLayout.getColor(), loc, false, this) ,this);
            }
        }

    }

    public boolean checkForGameOver() {
        TeamColor opposingColor = this.getOpposingColor().get(getTurn());
        boolean[][] possibleSpaces = getPossibleMovesByColor(opposingColor, this.getSpaces(), false);

        for (int i = 0; i < possibleSpaces.length; i++) {
            for (int j = 0; j < possibleSpaces[i].length; j++) {
                if (possibleSpaces[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public TeamColor getTurn() {
        return this.turn;
    }

    public void swapTurn() {
        this.turn = opposingColors.get(this.turn);
    }

    public Piece getPieceByPosition(BoardLocation location) {
        if (location.getX() > 8 || location.getX() < 0 || location.getY() > 8 || location.getY() < 0)
            return null;
        return this.spaces[location.getX()][location.getY()].getPiece();
    }

    public void closeLobby(String alert, int type) {
        this.players.forEach((id, p)->{
            p.getThread().showAlert(alert, type);
        });
        Server.clearLobby(this.gameKey);
    }

    public void setPiece(Piece p, BoardLocation oldLoc, BoardLocation newLoc, Piece target) {
        Piece oldP = this.spaces[oldLoc.getX()][oldLoc.getY()].getPiece();
        Piece newP = this.spaces[newLoc.getX()][newLoc.getY()].getPiece();
        if (
                (oldP != null) &&
                (newP != null) && (oldP.getColor() == newP.getColor() && oldP.getPieceType() == PieceType.KING && newP.getPieceType() == PieceType.ROOK)) {
            if (oldP.getLocation().getY() > newP.getLocation().getY()) { // piece is WHITE
                this.spaces[oldP.getLocation().getX()][oldP.getLocation().getY() - 2].setPiece(oldP);
                this.spaces[newP.getLocation().getX()][newP.getLocation().getY() + 2].setPiece(newP);
            } else {                                                     // piece is BLACK
                this.spaces[oldP.getLocation().getX()][oldP.getLocation().getY() + 2].setPiece(oldP);
                this.spaces[newP.getLocation().getX()][newP.getLocation().getY() - 2].setPiece(newP);
            }
            this.spaces[oldLoc.getX()][oldLoc.getY()].setPiece(null);
            this.spaces[newLoc.getX()][newLoc.getY()].setPiece(null);
        } else {
            this.spaces[oldLoc.getX()][oldLoc.getY()].setPiece(null);
            this.spaces[newLoc.getX()][newLoc.getY()].setPiece(p);
            this.spaces[newLoc.getX()][newLoc.getY()].getPiece().pawnMoved();
            if (!((p.getPieceType() != PieceType.PAWN) || (newLoc.getX()!=7 && newLoc.getX() !=0))) {
                this.spaces[newLoc.getX()][newLoc.getY()].setPiece(new Piece(PieceType.QUEEN, p.getColor(), p.getLocation(), true, this));
            }
        }

        updatePlayers(this.gameKey, target != null ? target.getPieceType().getKey() : 1);
        if (this.checkForGameOver()) {
            closeLobby(this.getTurn() + " has won the match!", 1);
        } else {
            swapTurn();
        }

    }


    public Space[][] getSpaces() {return this.spaces;}
    public boolean[][] getPossibleMovesByColor(TeamColor color) {
        boolean[][] moveSpaces = new boolean[8][8];
        for (Space[] space : spaces) {
            for (int j = 0; j < space.length; j++) {
                Space s = space[j];
                if (s.isEmpty())
                    continue;
                if (s.getPiece().getColor() != color)
                    continue;
                Piece p = s.getPiece();

                boolean[][] moves = Piece.getPossibleMoves(p, true);
                for (int x = 0; x < moves.length; x++) {
                    for (int y = 0; y < moves[x].length; y++) {
                        if (moves[x][y]) {
                            moveSpaces[x][y] = true;
                        }
                    }
                }

            }
        }
        return moveSpaces;
    }

    public HashMap<TeamColor, TeamColor> getOpposingColor() {
        return opposingColors;
    }
    public boolean[][] getPossibleMovesByColor(TeamColor color, Space[][] sps, boolean targetFriend) {
        boolean[][] moveSpaces = new boolean[8][8];
        for (Space[] space : sps) {
            for (Space s : space) {
                if (s.isEmpty())
                    continue;
                if (s.getPiece().getColor() != color)
                    continue;
                Piece p = s.getPiece();

                boolean[][] moves = p.getPossibleMoves(p, targetFriend);
                for (int x = 0; x < moves.length; x++) {
                    for (int y = 0; y < moves[x].length; y++) {
                        if (moves[x][y]) {
                            moveSpaces[x][y] = true;
                            System.out.println("SPACE" + s.getLocation().getX() + ". " + s.getLocation().getY());
                        }
                    }
                }

            }
        }
        return moveSpaces;
    }
    public boolean[][] getPossibleMovesByColor(TeamColor color, Space[][] sps) {
        boolean[][] moveSpaces = new boolean[8][8];
        for (Space[] space : sps) {
            for (int j = 0; j < space.length; j++) {
                Space s = space[j];
                if (s.isEmpty())
                    continue;
                if (s.getPiece().getColor() != color)
                    continue;
                Piece p = s.getPiece();

                boolean[][] moves = p.getPossibleMoves(p, true, sps);
                for (int x = 0; x < moves.length; x++) {
                    for (int y = 0; y < moves[x].length; y++) {
                        if (moves[x][y]) {
                            moveSpaces[x][y] = true;
                        }
                    }
                }

            }
        }
        return moveSpaces;
    }

    public boolean isInCheck(TeamColor targetColor, Space[][] spaces, boolean[][] moveSpaces) {
        for (int i = 0; i < moveSpaces.length; i++) {
            for (int j = 0; j < moveSpaces[i].length; j++) {
                if (moveSpaces[i][j]) {
                    Space s = spaces[i][j];
                    if (s.isEmpty())
                        continue;
                    Piece p = s.getPiece();
                    if (p.getColor() != targetColor)
                        continue;
                    if (p.getPieceType() != PieceType.KING)
                        continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static void simulateMove(Space[][] simSpaces, Piece p, BoardLocation newLocation, BoardLocation oldLocation) {
        if (p == null)
            return;
        Space s = simSpaces[newLocation.getX()][newLocation.getY()];
        Space old = simSpaces[oldLocation.getX()][oldLocation.getY()];
        s.setPiece(p);
        old.setPiece(null);
    }
    public boolean isAPlayer(String id)  {
        return this.players.containsKey(id);
    }
    public void addPlayer(String id, Player player) {
        this.players.put(id, player);
        player.getThread().setLobbyKey(this.gameKey);
        System.out.println(id + " added to lobby: " + this.gameKey);
    }
    public void setPlayerColor(UUID id, TeamColor color) {
        this.players.get(id).setColor(color);
    }
    public TeamColor getColorByPlayer(String id) {
        return this.players.get(id).getColor();
    }

    public Integer[][][] getLayout() {
        Integer[][][] layout = new Integer[8][8][2];
        for (int i = 0; i < this.spaces.length; i++) {
            for (int i1 = 0; i1 < this.spaces[i].length; i1++) {
                Space space = spaces[i][i1];
                Piece piece = space.getPiece();
                if (piece!=null) {
                    layout[i][i1][0] = piece.getPieceType().getKey();
                    layout[i][i1][1] = piece.getColor().getKey();
                }
            }
        }
        return layout;
    }

    public void updatePlayers(String lobbyKey, int pieceType) {
        this.players.forEach((id, p)->{
            try {
                p.getThread().setLobbyKey(lobbyKey);
                p.getThread().updateGame(this.getLayout(), p.getColor().getKey(), pieceType);
                //p.getThread().setBoard(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
