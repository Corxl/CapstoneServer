package me.corxl.ChessServer.game.pieces;

import java.io.Serializable;

public enum PieceType implements Serializable {
    KING(new String[]{"pieces\\w_king_2x_ns.png", "pieces\\b_king_2x_ns.png"}, 1),
    QUEEN(new String[]{"pieces\\w_queen_2x_ns.png", "pieces\\b_queen_2x_ns.png"}, 2),
    PAWN(new String[]{"pieces\\w_pawn_2x_ns.png", "pieces\\b_pawn_2x_ns.png"}, 3),
    BISHOP(new String[]{"pieces\\w_bishop_2x_ns.png", "pieces\\b_bishop_2x_ns.png"}, 4),
    ROOK(new String[]{"pieces\\w_rook_2x_ns.png", "pieces\\b_rook_2x_ns.png"}, 5),
    KNIGHT(new String[]{"pieces\\w_knight_2x_ns.png", "pieces\\b_knight_2x_ns.png"}, 6);
    public final String[] fileLocation;
    private final int key;

    private PieceType(String[] fileLocation, int key) {
        this.fileLocation = fileLocation;
        this.key = key;

    }
    public int getKey() {
        return this.key;
    }

    public static PieceType getTypeByKey(int key) {
        for (int i = 0; i < PieceType.values().length; i++) {
            PieceType value = PieceType.values()[i];
            if (value.key==key)
                return value;
        }
        return null;
    }
}