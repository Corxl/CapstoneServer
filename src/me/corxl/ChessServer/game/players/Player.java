package me.corxl.ChessServer.game.players;

import me.corxl.ChessServer.game.pieces.TeamColor;

public class Player {
    private TeamColor color;
    private String name;
    private PlayerThread thread;

    public Player(TeamColor color, String name, PlayerThread thread) {
        this.color = color;
        this.name = name;
        this.thread = thread;
    }

    public TeamColor getColor() {
        return color;
    }

    public void setColor(TeamColor color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerThread getThread() {
        return thread;
    }

    public void setThread(PlayerThread thread) {
        this.thread = thread;
    }
}
