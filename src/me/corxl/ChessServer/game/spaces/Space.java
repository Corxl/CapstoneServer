package me.corxl.ChessServer.game.spaces;

import me.corxl.ChessServer.game.board.Board;
import me.corxl.ChessServer.game.pieces.Piece;

public class Space {
    private final BoardLocation location;
    private Piece currentPiece;
    private final String SELECT_FILE_LOCATION = "\\src\\main\\resources\\me\\corxl\\capstoneclient\\pieces\\select.png";
    private final String soundDir = System.getProperty("user.dir") + "\\src\\main\\resources\\me\\corxl\\capstoneclient\\sounds\\";
    private boolean isSelected = false;
    private Board board;

    public Space(Space space) {
        this.location = new BoardLocation(space.getLocation());
        if (space.getPiece() != null)
            this.currentPiece = new Piece(space.getPiece());
    }

    public Space(BoardLocation location, Board board) {
        this.location = location;
        this.board = board;
        this.currentPiece = null;
    }

    public Space(BoardLocation location, Piece piece, Board board) {
        this.location = location;
        this.board = board;
        currentPiece = piece;
    }
    public Piece getPiece() {
        return this.currentPiece;
    }

    public void setPiece(Piece p) {
        this.currentPiece = p;
        if (this.currentPiece != null) {
            p.setLocation(this.location);
        }

    }

    public BoardLocation getLocation() {
        return this.location;
    }

    public boolean isEmpty() {
        return this.currentPiece == null;
    }

    public boolean isOccupied() {
        return this.currentPiece != null;
    }


    public void clearSpace() {
        this.currentPiece = null;
    }
}
