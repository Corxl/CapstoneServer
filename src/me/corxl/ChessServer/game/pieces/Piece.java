package me.corxl.ChessServer.game.pieces;

import me.corxl.ChessServer.game.board.Board;
import me.corxl.ChessServer.game.spaces.BoardLocation;
import me.corxl.ChessServer.game.spaces.Space;

public class Piece {

    private final TeamColor color;
    private BoardLocation location;
    private final PieceType pieceType;
    private boolean pawnMoved = false;
    private transient Board board;

    public Piece(Piece piece) {
        this.pieceType = piece.getPieceType();
        this.color = piece.getColor();
        this.location = piece.getLocation();
        this.pawnMoved = piece.pawnMoved;
    }
    public Piece(PieceType pieceType, TeamColor color, BoardLocation location, boolean isPawnMoved, Board board) {
        this.color = color;
        this.location = location;
        this.pieceType = pieceType;
        this.pawnMoved = isPawnMoved;
        this.board = board;
    }
    public Board getBoard() {
        return this.board;
    }
    public boolean isBlack() {
        return this.color == TeamColor.BLACK;
    }
    public boolean isWhite() {
        return this.color == TeamColor.WHITE;
    }
    public boolean isPawnMoved() {
        return this.pawnMoved;
    }
    public void pawnMoved() {
        this.pawnMoved = true;
    }
    public TeamColor getColor() {
        return this.color;
    }
    public PieceType getPieceType() {
        return this.pieceType;
    }
    public void setLocation(BoardLocation location) {
        this.location = location;
    }
    public BoardLocation getLocation() {
        return this.location;
    }
    public static boolean[][] getPossibleMoves(Piece piece, boolean targetFriend, Space[][] spaces) {
        BoardLocation location = piece.getLocation();
        boolean[][] moveSpaces = new boolean[8][8];

        switch (piece.pieceType) {
            case PAWN:
                pawnMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case ROOK:
                rookMoves(moveSpaces, piece, spaces, targetFriend);
                break;
            case KING:
                kingMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case KNIGHT:
                knightMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case BISHOP:
                bishopMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case QUEEN:
                queenMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
        }
        return moveSpaces;
    }

    public static boolean[][] getPossibleMoves(Piece piece, boolean targetFriend) {
        Space[][] spaces = piece.getBoard().getSpaces();
        BoardLocation location = piece.getLocation();
        boolean[][] moveSpaces = new boolean[8][8];
        Board board = piece.getBoard();

        switch (piece.pieceType) {
            case PAWN:
                pawnMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case ROOK:
                rookMoves(moveSpaces, piece, spaces, targetFriend);
                break;
            case KING:
                kingMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case KNIGHT:
                knightMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case BISHOP:
                bishopMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
            case QUEEN:
                queenMoves(moveSpaces, location, piece, spaces, targetFriend);
                break;
        }

        for (int i = 0; i < moveSpaces.length; i++) {
            for (int j = 0; j < moveSpaces[i].length; j++) {
                if (moveSpaces[i][j]) {
                    Piece pieceCopy = new Piece(piece);
                    Space[][] spacesCopy = new Space[8][8];
                    for (int x = 0; x < spaces.length; x++) {
                        for (int y = 0; y < spaces[x].length; y++) {
                            spacesCopy[x][y] = new Space(spaces[x][y]);
                        }
                    }
                    // Simulate spaces, if the king is still in check after that move, remove it from the possible move spaces.
                    BoardLocation oldLoc = new BoardLocation(piece.getLocation());
                    BoardLocation newLoc = new BoardLocation(i, j);
                    Board.simulateMove(spacesCopy, pieceCopy, newLoc, oldLoc);
                    boolean[][] possMoves = board.getPossibleMovesByColor(board.getOpposingColor().get(pieceCopy.getColor()), spacesCopy);
//                    System.out.println("_-_-_-_-_");
//                    for (int k = 0; k < possMoves.length; k++) {
//                        System.out.println(Arrays.toString(possMoves[k]));
//                    }
//                    for (int k = 0; k < spacesCopy.length; k++) {
//                        for (int x = 0; x < spacesCopy.length; x++) {
//                            System.out.print((spacesCopy[k][x].getPiece() == null ? null : spacesCopy[k][x].getPiece().getPieceType()) + ", ");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println("_-_-_-_-_");
                    boolean isChecked = board.isInCheck(pieceCopy.getColor(), spacesCopy, possMoves);
                    if (!isChecked) {
                        moveSpaces[i][j] = true;
                        System.out.println(i + ", " + j);
                    } else {
                        moveSpaces[i][j] = false;
                    }
                }
            }
        }
        return moveSpaces;

    }

    public boolean[][] getPossibleMoves(boolean targetFriend) {
        Space[][] spaces = this.getBoard().getSpaces();
        BoardLocation location = this.getLocation();
        boolean[][] moveSpaces = new boolean[8][8];
        Board board = this.getBoard();

        switch (this.pieceType) {
            case PAWN:
                pawnMoves(moveSpaces, location, this, spaces, targetFriend);
                break;
            case ROOK:
                rookMoves(moveSpaces, this, spaces, targetFriend);
                break;
            case KING:
                kingMoves(moveSpaces, location, this, spaces, targetFriend);
                break;
            case KNIGHT:
                knightMoves(moveSpaces, location, this, spaces, targetFriend);
                break;
            case BISHOP:
                bishopMoves(moveSpaces, location, this, spaces, targetFriend);
                break;
            case QUEEN:
                queenMoves(moveSpaces, location, this, spaces, targetFriend);
                break;
        }

        for (int i = 0; i < moveSpaces.length; i++) {
            for (int j = 0; j < moveSpaces[i].length; j++) {
                if (moveSpaces[i][j]) {
                    Piece pieceCopy = new Piece(this);
                    Space[][] spacesCopy = new Space[8][8];
                    for (int x = 0; x < spaces.length; x++) {
                        for (int y = 0; y < spaces[x].length; y++) {
                            spacesCopy[x][y] = new Space(spaces[x][y]);
                        }
                    }
                    // Simulate spaces, if the king is still in check after that move, remove it from the possible move spaces.
                    BoardLocation oldLoc = new BoardLocation(this.getLocation());
                    BoardLocation newLoc = new BoardLocation(i, j);
                    board.simulateMove(spacesCopy, pieceCopy, newLoc, oldLoc);
                    boolean[][] possMoves = board.getPossibleMovesByColor(board.getOpposingColor().get(pieceCopy.getColor()), spacesCopy);
                    boolean isChecked = board.isInCheck(pieceCopy.getColor(), spacesCopy, possMoves);
                    if (!isChecked) {
                        moveSpaces[i][j] = true;
                    } else {
                        moveSpaces[i][j] = false;
                    }
                }
            }
        }
        return moveSpaces;
    }

    private static void bishopMoves(boolean[][] moveSpaces, BoardLocation location, Piece piece, Space[][] spaces, boolean targetFriend) {
        int[][] relativeSpaces = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        for (int i = 0; i < relativeSpaces.length; i++) {
            int xMod = relativeSpaces[i][0];
            int yMod = relativeSpaces[i][1];
            int targetX = location.getX() + xMod;
            int targetY = location.getY() + yMod;
            while (targetX >= 0 && targetX < 8 && targetY >= 0 && targetY < 8) {
                if (spaces[targetX][targetY].isEmpty())
                    moveSpaces[targetX][targetY] = true;
                else {
                    if (spaces[targetX][targetY].getPiece().getColor() != piece.getColor() || targetFriend)
                        moveSpaces[targetX][targetY] = true;
                    break;
                }
                targetX += xMod;
                targetY += yMod;
            }
        }
    }

    private static void queenMoves(boolean[][] moveSpaces, BoardLocation location, Piece piece, Space[][] spaces, boolean targetFriend) {
        rookMoves(moveSpaces, piece, spaces, targetFriend);
        bishopMoves(moveSpaces, location, piece, spaces, targetFriend);
    }

    private static void knightMoves(boolean[][] moveSpaces, BoardLocation location, Piece piece, Space[][] spaces, boolean targetFriend) {

        int[][] targetModifiers = {{1, 2}, {2, 1}, {-1, 2}, {1, -2}, {2, -1}, {-2, 1}, {-2, -1}, {-1, -2}};
        for (int i = 0; i < targetModifiers.length; i++) {
            int x = location.getX();
            int y = location.getY();
            int targetX = x - targetModifiers[i][0];
            int targetY = y - targetModifiers[i][1];
            if (targetX >= 0 && targetX < 8 && targetY >= 0 && targetY < 8) {
                if (spaces[targetX][targetY].isEmpty())
                    moveSpaces[targetX][targetY] = true;
                else {
                    if (spaces[targetX][targetY].getPiece().getColor() != piece.getColor() || targetFriend)
                        moveSpaces[targetX][targetY] = true;
                }
            }
        }
    }

    private static void kingMoves(boolean[][] moveSpaces, BoardLocation location, Piece piece, Space[][] spaces, boolean targetFriend) {
        try {
            int[][] relativeSpaces = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            for (int i = 0; i < relativeSpaces.length; i++) {
                int xMod = relativeSpaces[i][0];
                int yMod = relativeSpaces[i][1];
                if (location.getX() + xMod >= 0 && location.getX() + xMod < 8) {
                    if (spaces[location.getX() + xMod][location.getY()].isEmpty())
                        moveSpaces[location.getX() + xMod][location.getY()] = true;
                    else {
                        if (spaces[location.getX() + xMod][location.getY()].getPiece().getColor() != piece.getColor() || targetFriend)
                            moveSpaces[location.getX() + xMod][location.getY()] = true;
                    }
                    if (spaces[location.getX() + xMod][location.getY() + 1].isEmpty())
                        moveSpaces[location.getX() + xMod][location.getY() + 1] = true;
                    else {
                        if (spaces[location.getX() + xMod][location.getY() + 1].getPiece().getColor() != piece.getColor() || targetFriend)
                            moveSpaces[location.getX() + xMod][location.getY() + 1] = true;
                    }
                    if (location.getY() - 1 >= 0 && location.getY() - 1 < 8) {
                        if (spaces[location.getX() + xMod][location.getY() - 1].isEmpty())
                            moveSpaces[location.getX() + xMod][location.getY() - 1] = true;
                        else {
                            if (spaces[location.getX() + xMod][location.getY() - 1].getPiece().getColor() != piece.getColor() || targetFriend)
                                moveSpaces[location.getX() + xMod][location.getY() - 1] = true;
                        }
                    }
                }
                if (!(location.getY() + yMod < 0 || location.getY() + yMod >= 8)) {
                    if (spaces[location.getX()][location.getY() + yMod].isEmpty())
                        moveSpaces[location.getX()][location.getY() + yMod] = true;
                    else {
                        if (spaces[location.getX()][location.getY() + yMod].getPiece().getColor() != piece.getColor() || targetFriend)
                            moveSpaces[location.getX()][location.getY() + yMod] = true;
                    }
                }
            }
            if (piece.isPawnMoved()) {
                return;
            }
            int castleDirection = piece.getColor()==TeamColor.WHITE ? -1 : 1;
            int Y = location.getY() + (castleDirection * 3);
            Piece rook = spaces[location.getX()][Y].getPiece();
            if (rook == null || rook.getPieceType() != PieceType.ROOK || rook.isPawnMoved()) {
                return;
            }
            if (spaces[location.getX()][location.getY() + castleDirection].getPiece() != null || spaces[location.getX()][location.getY() + (castleDirection * 2)].getPiece() != null)
                return;
            moveSpaces[location.getX()][Y] = true;
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }

    private static void pawnMoves(boolean[][] moveSpaces, BoardLocation location, Piece piece, Space[][] spaces, boolean targetFriend) {
        try {
            int modifier = piece.isWhite() ? -1 : 1;
            int pawnMovedModifier = !piece.pawnMoved ? 2 : 1;
            if (!(location.getY() - 1 < 0) && (location.getX() + (modifier) >=0)) {
                Space left = spaces[location.getX() + (modifier)][location.getY() - 1];
                if (!left.isEmpty())
                    if (left.getPiece().getColor() != piece.getColor() || targetFriend)
                        moveSpaces[location.getX() + (modifier)][location.getY() - 1] = true;
            }
            if ((location.getX() + (modifier) >=0) && !(location.getY() + 1 >= spaces[location.getX() + (modifier)].length)) {
                Space right = spaces[location.getX() + (modifier)][location.getY() + 1];
                if (!right.isEmpty())
                    if (right.getPiece().getColor() != piece.getColor() || targetFriend)
                        moveSpaces[location.getX() + (modifier)][location.getY() + 1] = true;
            }

            for (int i = 1; i <= pawnMovedModifier; i++) {
                if (location.getX() + (modifier * i) >= 0) {
                    Space s = spaces[location.getX() + (modifier * i)][location.getY()];
                    if (s.isEmpty()) {
                        moveSpaces[location.getX() + (modifier * i)][location.getY()] = true;
                    } else {
                        if (targetFriend) {
                            moveSpaces[location.getX() + (modifier * i)][location.getY()] = true;
                        }
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignore) {}
    }

    private static void rookMoves(boolean[][] moveSpaces, Piece piece, Space[][] spaces, boolean targetFriend) {
        for (int i = piece.getLocation().getX() + 1; i < spaces.length; i++) {
            if (spaces[i][piece.getLocation().getY()].isEmpty())
                moveSpaces[i][piece.getLocation().getY()] = true;
            else {
                if (spaces[i][piece.getLocation().getY()].getPiece().getColor() != piece.getColor() || targetFriend) {
                    moveSpaces[i][piece.getLocation().getY()] = true;
                }
                break;
            }
        }
        for (int i = piece.getLocation().getX() - 1; i >= 0; i--) {
            if (spaces[i][piece.getLocation().getY()].isEmpty())
                moveSpaces[i][piece.getLocation().getY()] = true;
            else {
                if (spaces[i][piece.getLocation().getY()].getPiece().getColor() != piece.getColor() || targetFriend) {
                    moveSpaces[i][piece.getLocation().getY()] = true;
                }
                break;
            }
        }
        for (int i = piece.getLocation().getY() - 1; i >= 0; i--) {
            if (spaces[piece.getLocation().getX()][i].isEmpty())
                moveSpaces[piece.getLocation().getX()][i] = true;
            else {
                if (spaces[piece.getLocation().getX()][i].getPiece().getColor() != piece.getColor() || targetFriend) {
                    moveSpaces[piece.getLocation().getX()][i] = true;
                }
                break;
            }
        }
        for (int i = piece.getLocation().getY() + 1; i < 8; i++) {
            if (spaces[piece.getLocation().getX()][i].isEmpty())
                moveSpaces[piece.getLocation().getX()][i] = true;
            else {
                if (spaces[piece.getLocation().getX()][i].getPiece().getColor() != piece.getColor() || targetFriend) {
                    moveSpaces[piece.getLocation().getX()][i] = true;
                }
                break;
            }
        }
    }
}
