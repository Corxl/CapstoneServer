package me.corxl.ChessServer.game.spaces;

import java.io.Serializable;

public class BoardLocation implements Serializable {

    private final int x, y;

    public BoardLocation(BoardLocation location) {
        this.x = location.getX();
        this.y = location.getY();
    }

    public BoardLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}