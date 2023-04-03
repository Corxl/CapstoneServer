package me.corxl.ChessServer.game.pieces;

import java.io.Serializable;

public enum TeamColor implements Serializable {
    WHITE(1), BLACK(2);

    private final int key;

    TeamColor(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public static TeamColor getTypeByKey(int key) {
        for (int i = 0; i < TeamColor.values().length; i++) {
            TeamColor value = TeamColor.values()[i];
            if (value.key==key)
                return value;
        }
        return null;
    }
}
